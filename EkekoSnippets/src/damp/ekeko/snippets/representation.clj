(ns
  ^{:doc "Core functionality related to the Snippet datatype used in snippet-driven queries."
    :author "Coen De Roover, Siltvani"}
   damp.ekeko.snippets.representation
  (:require [damp.ekeko.snippets 
             [util :as util]])
    (:require [damp.ekeko.jdt 
             [astnode :as astnode]]))


;; Snippet Datatype
;; ----------------

; Datatype representing a code snippet that can be matched against Java projects.

; For each AST node of the code snippet, a matching AST node has to be found in the Java project.
; To this end, an Ekeko query is generated that can be launched against the project. 
; The logic variables in the query correspond to invididual AST nodes of the snippet. 
; They will be bound to matching AST nodes in the project. 
; The actual conditions that are generated for the query depend on how each AST node of the snippet
; is to be matched (e.g., more lenient, or rather strict).

; members of the Snippet datatype:
; - ast: complete AST for the original code snippet
; - ast2var: map from an AST node of the snippet to the logic variable that is to be bound to its match
; - ast2groundf: map from an AST node of the snippet to a function that generates "grounding" conditions. 
;   These will ground the corresponding logic variable to an AST node of the Java project
;   format of the function is list (:type arguments)
; - ast2constrainf:  map from an AST node of the snippet to a function that generates "constraining" conditions. 
;   These will constrain the bindings for the corresponding logic variable to an AST node of the Java project
;   that actually matches the AST node of the snippet. 
;   format of the function is list (:type arguments)
; - var2ast: map from a logic variable to the an AST node which is bound to its match
; - var2uservar: map from logic variable to the user defined logic variable 
; - node2usernode: map from node (= list wrapper) to the list rewrite 
; - userquery: user defined logic conditions 
;   format in quote '((...)(....))


(defrecord 
  Snippet
  [ast ast2var ast2groundf ast2constrainf var2ast var2uservar node2usernode userquery])

(defn 
  snippet-var-for-node 
  "For the given AST node of the given snippet, returns the name of the logic
   variable that will be bound to a matching AST node from the Java project."
  [snippet snippet-node]
  (get-in snippet [:ast2var snippet-node]))

(defn 
  snippet-grounder-for-node
  "For the given AST node of the given snippet, returns the function type
   that will generate grounding conditions for the corresponding logic variable."
  [snippet template-ast]
  (first (get-in snippet [:ast2groundf template-ast])))

(defn 
  snippet-constrainer-for-node
  "For the given AST node of the given snippet, returns the function type
   that will generate constraining conditions for the corresponding logic variable."
  [snippet template-ast]
  (first (get-in snippet [:ast2constrainf template-ast])))

(defn 
  snippet-grounder-args-for-node
  "For the given AST node of the given snippet, returns the list of function arguments
   that will generate grounding conditions for the corresponding logic variable."
  [snippet template-ast]
  (rest (get-in snippet [:ast2groundf template-ast])))

(defn 
  snippet-constrainer-args-for-node
  "For the given AST node of the given snippet, returns the function arguments
   that will generate constraining conditions for the corresponding logic variable."
  [snippet template-ast]
  (rest (get-in snippet [:ast2constrainf template-ast])))

(defn 
  snippet-node-for-var 
  "For the logic variable of the given snippet, returns the AST node  
   that is bound to a matching logic variable."
  [snippet snippet-var]
  (get-in snippet [:var2ast snippet-var]))

(defn 
  snippet-uservar-for-var 
  "For the given logic var of the given snippet, returns the name of the user defined logic variable."
  [snippet snippet-var]
  (get-in snippet [:var2uservar snippet-var]))

(defn 
  snippet-usernode-for-node 
  "For the given node (list wrapper) of the given snippet, returns the user defined node (list rewrite)."
  [snippet snippet-ast]
  (get-in snippet [:node2usernode snippet-ast]))

(defn 
  snippet-node-with-member
  "Returns node (= wrapper of NodeList) which it's :value (= NodeList) has member mbr."
  [mbr]
  (first (astnode/node-propertyvalues (.getParent mbr))))

(defn 
  snippet-node-with-value
  "Returns node (= wrapper of NodeList) which has :value = value."
  [value]
  (snippet-node-with-member (first value)))

(defn
  snippet-value-for-node
  "Return :value of the given node (= wrapper of NodeList) or value of usernode if it exist."
  [snippet node]
  (let [list-rewrite (snippet-usernode-for-node snippet node)]
    (if (nil? list-rewrite)
      (:value node)
      (util/rewritten-list-from-listrewrite list-rewrite))))

(defn 
  snippet-nodes
  "Returns all AST nodes of the given snippet."
  [snippet]
  (keys (:ast2var snippet)))

(defn 
  snippet-vars
  "Returns the logic variables that correspond to the AST nodes
   of the given snippet. These variables will be bound to matching
   AST nodes from the queried Java project."
  [snippet]
  (vals (:ast2var snippet)))

(defn 
  snippet-uservars
  "Returns the logic variables defined by users of the given snippet."
  [snippet]
  (vals (:var2uservar snippet)))

(defn 
  snippet-userqueries
  "Returns the logic conditions defined by users of the given snippet."
  [snippet]
  (let [query (:userquery snippet)]
        (if (nil? query)
          '()
          query)))

;; Constructing Snippet instances
;; ------------------------------


(defn 
  jdt-node-as-snippet
  "Interpretes the given JDT ASTNode as a snippet with default matching 
   strategies (i.e., grounding=:minimalistic, constaining=:exact)
   for the values of its properties."
  [n]
  (defn assoc-snippet-value [snippet value]
    (let [lvar (util/gen-readable-lvar-for-value value)]
      (->
        snippet
        (assoc-in [:ast2var value] lvar)
        (assoc-in [:ast2groundf value] (list :minimalistic))
        (assoc-in [:ast2constrainf value] (list :exact))
        (assoc-in [:var2ast lvar] value))))
  (let [snippet (atom (Snippet. n {} {} {} {} {} {} {}))]
    (util/walk-jdt-node 
      n
      (fn [astval] (swap! snippet assoc-snippet-value astval))
      (fn [lstval] 
        (swap! snippet assoc-snippet-value lstval)
        (let [rawlst (:value lstval)
              rawlstvar (util/gen-readable-lvar-for-value rawlst)]
          (swap! snippet assoc-in [:ast2var rawlst] rawlstvar)))
      (fn [primval]  (swap! snippet assoc-snippet-value primval))
      (fn [nilval] (swap! snippet assoc-snippet-value nilval)))
    @snippet))
  

;; Updating Snippet instances
;;-----------------------------

(defn 
  add-node-to-snippet
  "Add node to snippet."
  [snippet n]
  (defn assoc-snippet-value [snippet value]
    (let [lvar (util/gen-readable-lvar-for-value value)]
      (->
        snippet
        (assoc-in [:ast2var value] lvar)
        (assoc-in [:ast2groundf value] (list :minimalistic))
        (assoc-in [:ast2constrainf value] (list :exact))
        (assoc-in [:var2ast lvar] value))))
  (let [snippet (atom snippet)]
    (util/walk-jdt-node 
      n
      (fn [astval] (swap! snippet assoc-snippet-value astval))
      (fn [lstval] 
        (swap! snippet assoc-snippet-value lstval)
        (let [rawlst (:value lstval)
              rawlstvar (util/gen-readable-lvar-for-value rawlst)]
          (swap! snippet assoc-in [:ast2var rawlst] rawlstvar)))
      (fn [primval]  (swap! snippet assoc-snippet-value primval))
      (fn [nilval] (swap! snippet assoc-snippet-value nilval)))
    @snippet))

(defn 
  remove-node-from-snippet
  "Clear grounding & constraining function for all child of a given node in snippet."
  [snippet node]
  (defn update-snippet-value [snippet value]
    (update-in snippet [:ast2groundf value] (fn [x] (list :epsilon)))
    (update-in snippet [:ast2constrainf value] (fn [x] (list :epsilon))))
  (let [snippet (atom snippet)]
    (util/walk-jdt-node 
      node
      (fn [astval] (swap! snippet update-snippet-value astval))
      (fn [lstval] (swap! snippet update-snippet-value lstval))
      (fn [primval]  (swap! snippet update-snippet-value primval))
      (fn [nilval] (swap! snippet update-snippet-value nilval)))
    @snippet))


;; walk through snippet
;;-----------------------------

(defn 
  walk-jdt-node-of-snippet
  "Recursive descent through a JDT node, applying given functions to the encountered 
   ASTNode instances and Ekeko wrappers for their property values.
   With addition get new property value from snippet :node2usernode if it exist."
  [snippet n node-f list-f primitive-f null-f]
  (loop
    [nodes (list n)]
    (when-not (empty? nodes)
      (let [val (first nodes)
            others (rest nodes)]
        (cond 
          (astnode/ast? val)
          (do
            (node-f val)
            (recur (concat (astnode/node-propertyvalues val) others)))
          (astnode/lstvalue? val)
          (do 
            (list-f val)
            (recur (concat (snippet-value-for-node snippet val) others)))
          (astnode/primitivevalue? val)
          (do
            (primitive-f val)
            (recur others))
          (astnode/nilvalue? val)
          (do
            (null-f val)
            (recur others)))))))


