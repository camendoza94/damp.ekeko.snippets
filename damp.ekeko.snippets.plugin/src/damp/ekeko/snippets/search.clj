(ns 
  ^{:doc "(Genetic) search for template specifications."
  :author "Coen De Roover"}
  damp.ekeko.snippets.search
  (:import 
    [damp.ekeko JavaProjectModel]
    [org.eclipse.jface.text Document]
    [org.eclipse.text.edits TextEdit]
    [org.eclipse.jdt.core ICompilationUnit IJavaProject]
    [org.eclipse.jdt.core.dom BodyDeclaration Expression Statement ASTNode ASTParser AST CompilationUnit]
    [org.eclipse.jdt.core.dom.rewrite ASTRewrite])
  (:require [clojure.core.logic :as cl])
  (:require [damp.ekeko]
            [damp.ekeko.jdt
             [astnode :as astnode]
             [rewrites :as rewrites]])
  (:require [damp.ekeko.snippets 
             [snippet :as snippet]
             [snippetgroup :as snippetgroup]
             [persistence :as persistence]
             [querying :as querying]
             [matching :as matching]
             [operators :as operators]
             [operatorsrep :as operatorsrep]
             ]))


;; Problem representation
;; State corresponds to templategroup (can use sequence of operators later, problem is that there arguments cannot easily cross over)


(defrecord
  VerifiedMatches
  [positives negatives])

(defn
  make-verified-matches
  "Create a record of verified matches, consisting of a number of positive and negative matches.
   The positive ones are those that the resulting template must match;
   the negative one are those it may not match."
  [positives negatives]
  (VerifiedMatches. (into #{} positives)
                    (into #{} negatives)))


(defn
  templategroup-matches
  "Given a templategroup, look for all of its matches in the code"
  [templategroup]
  (into #{} (eval (querying/snippetgroup-query|usingpredicates templategroup 'damp.ekeko/ekeko true)))) 

  
(defn 
  truep
  "True positives"
  [matches verifiedmatches]
  (clojure.set/intersection matches (:positives verifiedmatches)))

(defn 
  falsep
  "False positives"
  [matches verifiedmatches]
  (clojure.set/difference matches (:positives verifiedmatches)))

(defn
  falsen
  "False negatives"
  [matches verifiedmatches]
  (clojure.set/difference (:positives verifiedmatches) matches))
  
(defn 
  precision
  [matches verifiedmatches]
  (let [ctp (count (truep matches verifiedmatches))
        cfp (count (falsep matches verifiedmatches))]
    (if (= 0 (+ cfp ctp))
      0
      (/ ctp (+ cfp ctp)))))
  
(defn
  recall
  [matches verifiedmatches]
  (let [ctp (count (truep matches verifiedmatches))
        cfn (count (falsen matches verifiedmatches))]
    (/ ctp (+ ctp cfn))))
  
(defn
  fmeasure
  [matches verifiedmatches]
  (let [p (precision matches verifiedmatches)
        r (recall matches verifiedmatches)]
    (if 
      (= (+ p r) 0)
      0
      (* 2 (/ (* p r) (+ p r))))))


(defn
  make-fitness-function
  "Return a fitness function that calculates the F-measure/F1-score
   which compares the matches found by a template
   against the matches we want (or don't want)"
  [verifiedmatches]
  (fn [templategroup]
    (let [matches (templategroup-matches templategroup)]
      (fmeasure matches verifiedmatches))))


;; Search


;;assumes: arity of tuple corresponds to number of templategroups required
(defn
  templategroup-from-tuple
  ([tuple]
    (templategroup-from-tuple tuple "A population member"))
  ([tuple name]
    (snippetgroup/make-snippetgroup name
                                    (map matching/snippet-from-node tuple))))

(defn
  population-from-tuples
  "Generate an initial population of templates based on the desired matches"
  [matches]
  (map-indexed
    (fn [idx tuple] 
      (templategroup-from-tuple tuple (str "Offspring of tuple " idx)))
    matches))


(def
  registered-operators|search
  (filter (fn [op] 
            (let [id (operatorsrep/operator-id op)]
              (some #{id} 
                    ["replace-by-variable"
                     "add-directive-equals"
                     "replace-by-wildcard"
                     "add-directive-invokes"
                     "add-directive-invokedby"
                     "restrict-scope-to-child"
                     "relax-scope-to-child+"
                     "relax-scope-to-child*"
                     "relax-size-to-atleast"
                     "relax-scope-to-member"
                     "consider-set|lst"
                     ]
                    )))
          (operatorsrep/registered-operators)
          ))

(defn- rand-snippet [snippetgroup]
  (-> snippetgroup
    snippetgroup/snippetgroup-snippetlist
    rand-nth))
 
(defn
  mutate
  [snippetgroup]
  (let [group-copy (persistence/copy-snippetgroup snippetgroup)
        snippet (rand-snippet group-copy)
        value (rand-nth (snippet/snippet-nodes snippet))]
    (let [operators (operatorsrep/applicable-operators snippetgroup snippet value registered-operators|search)
          operator (rand-nth operators)
          operands (operatorsrep/operator-operands operator)]
      (let [operandvalues
            (map
              (fn [operand]
                (rand-nth
                  (operatorsrep/possible-operand-values|valid
                    snippetgroup snippet value operator operand)))
              operands)
            bindings
            (cons
              (operatorsrep/make-implicit-operandbinding-for-operator-subject snippetgroup snippet value operator)
              (map (fn [operand operandval]
                     (operatorsrep/make-binding operand snippetgroup snippet operandval))
                   operands
                   operandvalues))]
        (operatorsrep/apply-operator-to-snippetgroup group-copy 
                                                     snippet
                                                     value 
                                                     operator 
                                                     bindings)))))

(defn- node-expected-class
  "Returns the expected type of an ASTnode, more specifically, the type that the parent node expects.
   This is done by looking at the child type in the property descriptor of the parent node."
  [node]
  (let [pd (astnode/owner-property node)]
    (cond 
      (astnode/property-descriptor-simple? pd) (astnode/property-descriptor-value-class pd)
      (astnode/property-descriptor-child? pd) (astnode/property-descriptor-child-node-class pd)
      (astnode/property-descriptor-list? pd) (astnode/property-descriptor-element-node-class pd)
      :else (type node))))

(defn- rand-ast-node
  "Return a random AST node (that is not the root) in a snippet"
  [snippet]
  (let [node (rand-nth (snippet/snippet-nodes snippet))]
    (if (and 
          (astnode/ast? node) ; Because some nodes aren't AST nodes but property descriptors.. 
          (not= node (.getRoot node)))
      node
      (recur snippet))))

(defn- find-compatible-ast-pair
  "Given two snippets, find a pair of nodes in each snippet's AST
   such that they can be safely swapped without causing syntax issues"
  [snippet1 snippet2]
  (let [node1 (rand-ast-node snippet1)
        cls1 (node-expected-class node1)]
    (loop [node2 (rand-ast-node snippet2)
           i 0]
      (if (and 
            (instance? cls1 node2)
            (instance? (node-expected-class node2) node1))
        [node1 node2]
        (if (< i 20)
          (recur (rand-ast-node snippet2) (inc i)) ; Try again with another node2
          (find-compatible-ast-pair snippet1 snippet2) ; Seems like nothing is compatible with node1? Better start over.. 
          )))))

(defn
  crossover
  "Performs a crossover between two snippets:
   This means that two AST nodes are chosen at random, and both nodes (and their children) will be swapped.
   (In case this operation leads to invalid syntax, we try again..)
   Returns a vector containing the two crossed-over snippets"
  [snippetgroup1 snippetgroup2]
  (let
    [group-copy1 (persistence/copy-snippetgroup snippetgroup1)
     group-copy2 (persistence/copy-snippetgroup snippetgroup2)
     ; Get two random snippets
     snippet1 (rand-snippet group-copy1)
     snippet2 (rand-snippet group-copy2)
     ; Get two random AST nodes
     node-pair (find-compatible-ast-pair snippet1 snippet2)
     node1 (first node-pair)
     node2 (second node-pair) ]
    (println node1)
    (println node2)
    (let [new-snippet1 (operators/replace-node-with snippet1 node1 node2)
          new-snippet2 (operators/replace-node-with snippet2 node2 node1)]
      [(snippetgroup/snippetgroup-replace-snippet group-copy1 snippet1 new-snippet1)
       (snippetgroup/snippetgroup-replace-snippet group-copy2 snippet2 new-snippet2)])
    ))

(defn
  sort-by-fitness
  "Sort the templates in a population by the fitness function"
  [population fitnessf]
  (sort-by (fn [templategroup]
             (fitnessf templategroup))
           population))

(defn 
  select
  "Do tournament selection in a population:
   Given that the population is sorted from best fitness to worst, we pick a number 
   of random entries in the population, then return the best one from those entries.
   @param tournament-size  The number of random entries to pick"
  [population tournament-size]
  (let [size (count population)]
    (nth population
         (apply min (repeatedly tournament-size #(rand-int size))))))

(defn
  evolve
  "Look for a template that is able to match with a number of snippets, using genetic search
   @param verifiedmatches  There are the snippets we want to match with
   @param max-generations  Stop searching if we haven't found a good solution after this number of generations"
  [verifiedmatches max-generations]
  (let
    [fitness (make-fitness-function verifiedmatches)
     popsize (count verifiedmatches)
     tournament-size 7]
    (loop 
      [generation 0
       population (sort-by-fitness (population-from-tuples (:positives verifiedmatches)) fitness)]
      (let [best (last population)
            best-fitness (fitness best)]
        (println "Generation:" generation)
        (println "Highest fitness:" best-fitness)
        (println "Best specification:" (persistence/snippetgroup-string best))
        (when (< generation max-generations)
          (if
            (> best-fitness 0.9)
            (println "Success:" (persistence/snippetgroup-string best))
            (recur 
              (inc generation)
              (sort-by-fitness
                ; Produce the next generation using mutation, crossover and tournament selection
                (concat
                  ; Mutation
                  (repeatedly (* 1/2 (count population)) #(mutate (select population tournament-size)))
                  ; Crossover (Note that each crossover operation produces a pair)
                  (flatten (repeatedly (* 1/8 (count population)) #(crossover 
                                                                     (select population tournament-size)
                                                                     (select population tournament-size))))
                  ; Selection
                  (repeatedly (* 1/4 (count population)) #(select population tournament-size)))
                fitness))))))))

;; todo: applicable for equals: bestaande vars (of slechts 1 nieuwe)
;; todo: gewone a* search  

(comment
  (defmacro dbg[x]
    (if true
      `(let [x# ~x] (println "dbg:" x#) x#)
      x))
  (defmacro dbg[x y]
    (if true
      `(let [x# ~x] (println "dbg:" '~x "=" x# "---" ~y) x#)
      x))
  (use '(inspector-jay core))
  
  (def templategroup
       (persistence/slurp-from-resource "/resources/EkekoX-Specifications/invokes.ekt"))
  (def matches (templategroup-matches templategroup))
  (def verifiedmatches (make-verified-matches matches []))
  
  (inspect (querying/snippetgroup-query|usingpredicates templategroup 'damp.ekeko/ekeko true))
  
  (= 1 (precision matches verifiedmatches))
  (= 1 (recall matches verifiedmatches))
  
  (pmap (make-fitness-function verifiedmatches) (inspect (population-from-tuples matches)))
  
  ;MethodDeclaration - MethodInvocation (vars sorted .. cannot compare otherwise)
  (map (fn [tuples] (map (fn [tuple] (map class tuple)) tuples))
        (map templategroup-matches (population-from-tuples matches)))
  
  (evolve verifiedmatches 10)
  
  (let [pop (population-from-tuples matches)]
    [(first pop)
              (second pop)
              (crossover (first pop) (second pop))]
    0)
)