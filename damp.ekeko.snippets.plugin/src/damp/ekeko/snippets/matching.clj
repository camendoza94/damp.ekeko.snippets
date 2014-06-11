(ns 
  ^{:doc "Matching directives for template-based program transformation."
    :author "Coen De Roover, Siltvani"}
  damp.ekeko.snippets.matching
  (:require [clojure.core.logic :as cl])
  (:require [damp.ekeko.snippets 
             [directives :as directives]
             [util :as util]
             [snippet :as snippet]
             [snippetgroup :as snippetgroup]
             [parsing :as parsing]
             [runtime :as runtime]])
  (:require 
    [damp.ekeko [logic :as el]]
    [damp.ekeko.jdt 
     [astnode :as astnode]
     [ast :as ast]])
  (:import  [org.eclipse.jdt.core.dom.rewrite ASTRewrite]
            [org.eclipse.jdt.core.dom Expression Statement BodyDeclaration CompilationUnit ImportDeclaration]))
                          


(def ast `ast/ast)
(def has `ast/has)
(def value-raw `ast/value-raw)
(def value|null `ast/value|null)
(def to-modifier-keyword `runtime/to-modifier-keyword)
(def to-primitive-type-code `runtime/to-primitive-type-code)
(def to-assignment-operator `runtime/to-assignment-operator)
(def to-infix-expression-operator `runtime/to-infix-expression-operator)
(def to-prefix-expression-operator `runtime/to-prefix-expression-operator)
(def to-postfix-expression-operator `runtime/to-postfix-expression-operator)



(defn
  root-of-snippet?
  [value snippet]
  (= (snippet/snippet-root snippet) value))

(declare ast-primitive-as-expression)


;; Grounding Functions
;; -------------------


(declare directive-child)

(defn 
  ground-relativetoparent
  [snippet-val]
  (fn [snippet] 
    (let [var-match
          (snippet/snippet-var-for-node snippet snippet-val)] 
      (cond 
        ;root of snippet
        (root-of-snippet? snippet-val snippet)
        (let [snippet-ast-keyw
              (astnode/ekeko-keyword-for-class-of snippet-val)]
          `((~ast ~snippet-ast-keyw ~var-match)))
        ;member of list
        (astnode/valuelistmember? snippet-val)
        (let [bounddirectives
              (snippet/snippet-bounddirectives-for-node snippet snippet-val)
              list-owner      
              (snippet/snippet-list-containing snippet snippet-val)
              list-owner-directives 
              (snippet/snippet-bounddirectives-for-node snippet list-owner)
              list-match       
              (snippet/snippet-var-for-node snippet list-owner)
              list-raw        
              (:value list-owner)
              list-match-raw  
              (util/gen-readable-lvar-for-value list-raw)
              index-match     
              (.indexOf list-raw snippet-val)]
          ;could check for parent list directives that might already have ground the element
          ;but for now rely on operators to switch correctly between directives (and e.g., remove ground-relative-to-parent from all list elements)
          
          `((runtime/list-nth-element ~list-match ~index-match ~var-match))
          ;`((cl/fresh [~list-match-raw] 
          ;            (~value-raw ~list-match ~list-match-raw)
          ;            (el/equals ~var-match (.get ~list-match-raw ~index-match))))
          
          )
        
        
        (or 
          (astnode/ast? snippet-val)
          (astnode/lstvalue? snippet-val)
          (astnode/nilvalue? snippet-val)
          )
        (let [owner 
              (astnode/owner snippet-val)
              owner-match
              (snippet/snippet-var-for-node snippet owner)
              owner-property
              (astnode/owner-property snippet-val)
              owner-property-keyword
              (astnode/ekeko-keyword-for-property-descriptor owner-property)]
          `((~has ~owner-property-keyword ~owner-match ~var-match) 
             ))
        ;constraining the parent has already ground primitive values
        :default
        `()))))


(defn
  ground-relativetoparent+ 
  (;arity 0: reside within arbitraty depth for the match for their parent
    [snippet-val]
    (fn [snippet]
      (let [var-match (snippet/snippet-var-for-node snippet snippet-val)]
        ;ignore for root, as these are ground independent of a context
        (if 
          (root-of-snippet? snippet-val snippet)
          `(())
          (let [var-match-owner (snippet/snippet-var-for-node snippet (astnode/owner snippet-val))]
            `((ast/astorvalue-offspring+ ~var-match-owner ~var-match))))))))




(defn
  ground-element
  [val]
  (fn [template]
    (let [match
          (snippet/snippet-var-for-node template val)
          lst 
          (snippet/snippet-list-containing template val)
          list-match
          (snippet/snippet-var-for-node template lst)
          list-match-raw
          (util/gen-readable-lvar-for-value (:value list))]
      `((cl/fresh [~list-match-raw] 
                  (~value-raw ~list-match ~list-match-raw)
                  (el/contains ~list-match-raw ~match))))))


    


 
  ;(;arity 1: reside within arbitraty depth of given variable binding (should be ancestor)
  ;  [snippet-val ancestorvar]
  ;  (fn [snippet]
  ;    (let [var-match (snippet/snippet-var-for-node snippet snippet-val)
  ;          var (symbol ancestorvar)
  ;          var-match-owner (snippet/snippet-var-for-node snippet (astnode/owner snippet-val))]
  ;      `((runtime/ground-relativetoparent+|match-ownermatch-userarg  ~var-match ~var-match-owner ~var))))))
    

;; Constraining Functions
;; ----------------------

;;TODO: not in sync with node-filtered-ekeko-properties (or something like that)


(defn 
  is-ignored-property?
  [property-keyw]
  (= property-keyw :javadoc))



;todo: er is geen ast-conditie meer om type te checken, die moet er wel komen voor variabelen
(defn 
  constrain-exact
  [snippet-val]
  (fn [snippet]
    (let [var-match
          (snippet/snippet-var-for-node snippet snippet-val)]
      (cond
        ;constrain primitive-valued properties of node
        (astnode/ast? snippet-val)
        (let [snippet-properties 
              (astnode/node-ekeko-properties snippet-val)
              child-conditions 
              (mapcat
                (fn [[property-keyw retrievalf]]
                  (let [value     
                        (retrievalf snippet-val) 
                        var-value
                        (snippet/snippet-var-for-node snippet value)
                        property-descriptor
                        (astnode/node-property-descriptor-for-ekeko-keyword snippet-val property-keyw)]
                    (if
                      (or 
                        (is-ignored-property? property-keyw)
                        (not (astnode/property-descriptor-simple? property-descriptor)))
                      `()
                      `((~has ~property-keyw ~var-match ~var-value)))))
                snippet-properties)]
          `(~@child-conditions))
        ;constrain lists
        (astnode/lstvalue? snippet-val)
        (let [lst 
              (:value snippet-val)
              snippet-list-size 
              (.size lst)
              var-match-raw (util/gen-readable-lvar-for-value lst)]
          `(; (cl/fresh [~var-match-raw] 
            ;           (~value-raw ~var-match ~var-match-raw)
            ;           (el/equals ~snippet-list-size (.size ~var-match-raw))))
          
            (runtime/list-size ~var-match ~snippet-list-size)
          
          ))
        
        
        ;constrain primitive values
        (astnode/primitivevalue? snippet-val)
        (let [exp 
              (ast-primitive-as-expression (:value snippet-val))]
          `(;(ast/value|primitive ~var-match)
             (~value-raw ~var-match ~exp)))
        ;constrain null-values
        (astnode/nilvalue? snippet-val)
        `((~value|null ~var-match))))))


(declare string-represents-variable?)

(defn 
  conditions-variables
  [conditions]
  (filter string-represents-variable? (flatten conditions)))
                 
(declare snippet-value-multiplicity)
  
(defn
  constrain-lst|regexp 
  "Requires candidate matches to match a regular expresison. 
   Will ground elements. Therefore, elements should no longer have a grounding directive.
   Constraining conditions of elements will be feature as conditions inside the regular expression."
  [val]
  (fn [template]
    (let [var-match
          (snippet/snippet-var-for-node template val)
          var-match-qwalgraph
          (util/gen-lvar 'qgraph)
          var-match-qwalgraph-start
          (util/gen-lvar 'qgraphstart)
          var-match-qwalgraph-end
          (util/gen-lvar 'qgraphend)
          
          elements 
          (astnode/value-unwrapped val)
          
          idx-last 
          (dec (.size elements))
          
          element-conditions
           ;to skip to the first real element of the list
           (apply concat
               (map-indexed
                 (fn [idx element]
                   (let [var-elmatch                    
                         (snippet/snippet-var-for-node template element)
                         elmatchidx
                         (gensym 'elmatchidx)
                         elmatch 
                         (gensym 'elmatch)
                         
                         multiplicity
                         (snippet-value-multiplicity template element)
                         
                         ;these conditions no longer need to be included in the query 
                         elconditions 
                         (mapcat 
                           (fn [bounddirective]
                             (directives/snippet-bounddirective-conditions template bounddirective))
                           (snippet/snippet-bounddirectives-for-node template element))
                         
                         elconditionsvars
                         (conj (conditions-variables elconditions)
                               var-elmatch)

                         qcurrentcondition
                         (if 
                           (= "1" multiplicity)
                          `(damp.qwal/qcurrent [[~elmatchidx ~elmatch]]
                                               (cl/== ~var-elmatch ~elmatch) 
                                               ~@elconditions)
                          `(damp.qwal/qcurrent [[~elmatchidx ~elmatch]]
                                               (cl/fresh [~@elconditionsvars] ;perhaps not include uservars
                                                      (cl/== ~var-elmatch ~elmatch) 
                                                      ~@elconditions)))]
                     (condp = multiplicity
                       "1" 
                      `(~qcurrentcondition damp.qwal/q=>)
                       "+"
                       `((damp.qwal/q+ 
                         ~qcurrentcondition
                         damp.qwal/q=>
                         ))
                       "*"
                       `((damp.qwal/q* 
                          ~qcurrentcondition
                           damp.qwal/q=>
                          ))
                       )))
                 elements))]
      `((cl/fresh 
          [~var-match-qwalgraph ~var-match-qwalgraph-start ~var-match-qwalgraph-end]
          (damp.ekeko.snippets.runtime/value|list-qwal-start-end
            ~var-match
            ~var-match-qwalgraph
            ~var-match-qwalgraph-start
            ~var-match-qwalgraph-end)
          (damp.qwal/qwal   
            ~var-match-qwalgraph
            ~var-match-qwalgraph-start
            ~var-match-qwalgraph-end
            [] 
            damp.qwal/q=> ;to jump inside list 
            ~@element-conditions))))))
         
           

  (defn
    constrain-size|atleast
    "Requires candidate matches to have at least as many elements as the template list."
    [val]
    (fn [template]
      (let [var-match
            (snippet/snippet-var-for-node template val)
            lst
            (:value val)
            template-list-size 
            (.size lst)
            var-match-raw (util/gen-readable-lvar-for-value lst)]
        `(;(ast/value|list ~var-match)
           (cl/fresh [~var-match-raw] 
                     (~value-raw ~var-match ~var-match-raw)
                     (el/succeeds (>= (.size ~var-match-raw) ~template-list-size)))))))
  

  ;; Functions related to nodes that have been replaced by logic variable
  ;; --------------------------------------------------------------------

  (declare directive-replacedbyvariable)

  (defn 
    snippet-replacement-var-for-node 
    "For the given AST node of the given snippet, returns the name of the user logic
   variable that will be bound to a matching AST node from the Java project."
    [snippet node]
    (let [bds
          (snippet/snippet-bounddirectives-for-node snippet node)]
    (if-let [replaced-bd 
             (directives/bounddirective-for-directive 
               bds
               directive-replacedbyvariable)]
      (symbol (directives/directiveoperandbinding-value (nth (directives/bounddirective-operandbindings replaced-bd) 1))))))

  (defn 
    snippet-node-replaced-by-var?
    [snippet node]
    (boolean (snippet-replacement-var-for-node snippet node))) 

  (defn
    snippet-replacement-vars
    [snippet] 
    (distinct
      (remove nil? 
              (map
                (fn [node] 
                  (snippet-replacement-var-for-node snippet node))
                (snippet/snippet-nodes snippet)))))
  
  (defn
    string-represents-variable?
    [string]
    (@#'el/ekeko-lvar-sym? string))

  (defn
    snippet-vars-among-directivebindings-for-node
    "Returns all variables that feature as the binding for a directive operand of the node.
   Includes replacement vars."
    [snippet node]
    (let [bds (snippet/snippet-bounddirectives-for-node snippet node)]
      (mapcat
        (fn [bounddirective]
          (map symbol
               (filter string-represents-variable?
                       (map directives/directiveoperandbinding-value 
                            (directives/bounddirective-operandbindings bounddirective)))))
        bds)))

  (defn
    snippet-vars-among-directivebindings
    "Returns all variables that feature as the binding for a directive operand of the snippet.
   Includes replacement vars."
    [snippet]
    (mapcat
      (fn [node]
        (snippet-vars-among-directivebindings-for-node snippet node))
      (snippet/snippet-nodes snippet)))



  (defn
    snippetgroup-vars-among-directivebindings
    "Returns all variables that feature as the binding for a directive operand among the snippets in the snippet group.
   Includes replacement vars."
    [snippetgroup]
    (mapcat snippet-vars-among-directivebindings (snippetgroup/snippetgroup-snippetlist snippetgroup)))

  


  (defn
  constrain-replacedbyvariable
  "Constraining directive for nodes that have been replaced by a variable."
  [snippet-ast replacement-var-string]
  (fn [snippet]
     (let [var-match (snippet/snippet-var-for-node snippet snippet-ast)
             replacement-var (symbol replacement-var-string)]
         `((cl/== ~replacement-var ~var-match)))))


  (defn
    constrain-replacedbywildcard
    [snippet-val]
    (fn [snippet]
      `()))
    

  ;constraining/grounding will be done by parent regexp in which snippet-val resides
  (defn
    constrain-multiplicity|regexp
    [snippet-val multiplicity]
    (fn [snippet]
      `()))


  (defn
    constrain-equals
    "Constraining directive that will unify the node's match with the given variable."
    [snippet-ast var-string]
    (fn [snippet]
       (let [var-match (snippet/snippet-var-for-node snippet snippet-ast)
             var (symbol var-string)]
         `((cl/== ~var ~var-match)))))



  ;(defn
  ;  snippetgroup-uservars-for-information
  ;  [snippetgroup]
  ;  (mapcat snippet/snippet-uservars-for-information (snippetgroup-snippetlist snippetgroup)))

    



  ;(defn
  ;  cf-epsilon-with-variable
  ;  [snippet-val]
  ;  (fn [snippet] 
  ;     (let [var-match 
  ;           (snippet/snippet-var-for-node snippet snippet-val)
  ;           var-userprovided
  ;           (snippet/snippet-uservar-for-var snippet var-match)]
  ;       (if (nil? var-userprovided)
  ;         ((make-epsilon-function snippet-val) snippet)
  ;         (concat 
  ;           ((gf-node-exact snippet-val) snippet)
  ;           ((cf-variable snippet-val) snippet))))))


  ;; Functions involving wildcards


  (declare directive-replacedbywildcard)

  (defn 
    snippet-node-replaced-by-wilcard?
    [snippet node]
    (let [bds
          (snippet/snippet-bounddirectives-for-node snippet node)]
      (boolean
        (directives/bounddirective-for-directive 
          bds
          directive-replacedbywildcard))))
  

  (comment
    
(defn
  cf-exact-with-variable
  [snippet-val]
  (fn [snippet] 
    (concat 
      ((cf-exact snippet-val) snippet)
      ((cf-variable snippet-val) snippet))))

(defn
  cf-list-relax-size
  "Returns a function that will generate constraining conditions for the given property value of a code snippet.
   Conditions : - size of list no need to be the same"
  [snippet-val]
  (fn [snippet] 
    (internal-cf-list snippet snippet-val :notsamesize))) 
  
  (defn
  cf-list-relax-size-with-variable
  [snippet-val]
  (fn [snippet] 
    (concat 
      ((cf-list-relax-size snippet-val) snippet)
      ((cf-variable snippet-val) snippet))))

  (defn 
  cf-subtype
    "Returns a function that will generate constraining conditions for the given property value of a code snippet:
     For ASTNode instances: ((type-subtypematch-logictype :kind-of-node ?var-for-node ?var-for-node-match)
                               (ast :kind-of-node ?var-for-node)  
                               (has :property1 ?var-for-node ''primitive-valued-child-as-string''))
                               ....
     ?var-for-node is new logic variable."
  [snippet-ast]
  (fn [snippet]
    (let [snippet-keyw       (astnode/ekeko-keyword-for-class-of snippet-ast)
          snippet-properties (astnode/node-ekeko-properties snippet-ast)
          var-match          (snippet/snippet-var-for-node snippet snippet-ast)
          var-node           (util/gen-lvar)
          child-conditions 
              (for [[property-keyw retrievalf] 
                    (seq snippet-properties)
                    :let [value     (retrievalf snippet-ast) 
                          var-value (snippet/snippet-var-for-node snippet value)]]
                `(~has ~property-keyw ~var-node ~var-value))]
      `((cl/fresh [~var-node]
           (~ast ~snippet-keyw ~var-node)
           ~@child-conditions
             (runtime/type-relaxmatch-subtype ~snippet-keyw ~var-node ~var-match))))))

  (defn
  cf-subtype-with-variable
  [snippet-val]
  (fn [snippet] 
    (concat 
      ((cf-subtype snippet-val) snippet)
      ((cf-variable snippet-val) snippet))))

  (defn
  cf-relax-typeoftype
  [snippet-val]
  (fn [snippet]
      (let [cond-exact ((cf-exact snippet-val) snippet)
            snippet-var (snippet/snippet-var-for-node snippet snippet-val)
            type-var (snippet/snippet-var-for-node snippet (.getType snippet-val))]
      `((cl/conde 
            [~@cond-exact]
          [(el/equals ~snippet-var ~type-var)])))))

  (defn 
  cf-variable-declaration-with-initializer
    "Returns a function that will generate constraining conditions for the given property value of a code snippet:
     For ASTNode instances: (assignment-relaxmatch-variable-declaration ?var-for-node-match ?var-left ?var-right)"
  [snippet-ast]
  (fn [snippet]
    (let [var-match    (snippet/snippet-var-for-node snippet snippet-ast)
            var-left     (snippet/snippet-var-for-node snippet (.getLeftHandSide (.getExpression snippet-ast)))
            var-right    (snippet/snippet-var-for-node snippet (.getRightHandSide (.getExpression snippet-ast)))]
      `((runtime/assignment-relaxmatch-variable-declaration ~var-match ~var-left ~var-right)))))

  (defn
  cf-variable-declaration-with-variable
  [snippet-val]
  (fn [snippet] 
    (concat 
      ((cf-variable-declaration-with-initializer snippet-val) snippet)
      ((cf-variable snippet-val) snippet))))

  ;(defn 
  ;  ast-conditions
  ;  "Returns a list of logic conditions that will retrieve matches for the given snippet-ast in snippet."
  ;  [snippet ast]
  ;  (defn 
  ;    conditions
  ;    [ast-or-list]
  ;    (concat ((gf-exact ast-or-list) snippet)
  ;            ((cf-exact ast-or-list) snippet)))
  ;  (let [query (atom '())]
  ;    (util/walk-jdt-node 
  ;      ast
  ;      (fn [astval]  (swap! query concat (conditions astval)))
  ;      (fn [lstval] (swap! query concat (conditions lstval)))
  ;      (fn [primval] (swap! query concat (conditions primval)))
  ;      (fn [nilval] (swap! query concat (conditions nilval))))
  ;    @query))

  ;(defn 
  ;  cf-negated
  ;    "Returns a function that will generate constraining conditions for the given property value of a code snippet:
  ;     For ASTNode instances: (fails (all (node-conditions) (child-conditions)))."
  ;  [snippet-ast]
  ;  (fn [snippet]
  ;    (let [conditions-of-ast (ast-conditions snippet snippet-ast)]
  ;      `((el/fails (cl/all ~@conditions-of-ast))))))

  ;(defn
  ;  cf-negated-with-variable
  ;  [snippet-val]
  ;  (fn [snippet] 
  ;    (concat 
  ;      ((cf-negated snippet-val) snippet)
  ;      ((cf-variable snippet-val) snippet))))

  (defn 
  cf-relax-loop
    "Returns a function that will generate constraining conditions for the given property value of a code snippet:
     For ASTNode instances: ((ast :ForStatement/WhileStatement/DoStatement ?var-for-node-match)  
                               (has :property1 ?var-for-node-match ?var-for-child1-match)
                               (has :property2 ?var-for-node-match ''primitive-valued-child-as-string''))
                               ...."
  [snippet-ast]
  (fn [snippet]
    (let [snippet-keyw       (astnode/ekeko-keyword-for-class-of snippet-ast)
          snippet-properties (astnode/node-ekeko-properties snippet-ast)
          var-match          (snippet/snippet-var-for-node snippet snippet-ast)
          child-conditions 
              (for [[property-keyw retrievalf] 
                    (seq snippet-properties)
                    :let [value     (retrievalf snippet-ast) 
                          var-value (snippet/snippet-var-for-node snippet value)]]
                (if (and (not (is-ignored-property? property-keyw))
                           (not (= property-keyw :updaters))
                           (not (= property-keyw :initializers)))
                  `(ast/has ~property-keyw ~var-match ~var-value)))]
      `((cl/conde [(ast/ast :ForStatement ~var-match)]
                  [(ast/ast :WhileStatement ~var-match)]
                  [(ast/ast :DoStatement ~var-match)])
         ~@child-conditions))))

  (defn
  cf-relax-loop-with-variable
  [snippet-val]
  (fn [snippet] 
    (concat 
      ((cf-relax-loop snippet-val) snippet)
      ((cf-variable snippet-val) snippet))))


)


  (comment

  (defn
  make-constraining-function
  [type]
    (cond
      (= type :variable)
      cf-variable
    
      ;below has not been checked
      (= type :exact)
    cf-exact-with-variable
    (= type :any)
    cf-epsilon-with-variable
    (= type :child+)
    cf-list-relax-size-with-variable
    (= type :contains)
    cf-list-relax-size-with-variable
    (= type :contains-eq-size)
    cf-exact-with-variable
    (= type :contains-eq-order)
    cf-list-relax-size-with-variable
    (= type :contains-repetition)
    cf-list-relax-size-with-variable
    (= type :exact-variable)
    cf-exact-with-variable
    (= type :relax-type)
    cf-subtype-with-variable
    (= type :relax-typeoftype)
    cf-relax-typeoftype
    (= type :relax-assign)
    cf-variable-declaration-with-variable
    (= type :relax-branch)
    cf-exact-with-variable
    (= type :negated)
    cf-negated-with-variable
    (= type :relax-loop)
    cf-relax-loop-with-variable
    (= type :method-dec)
    cf-exact-with-variable
    (= type :var-dec)
    cf-exact-with-variable
    (= type :var-binding)
    cf-exact-with-variable
    (= type :var-type)
    cf-exact-with-variable
    (= type :var-qname)
    cf-exact-with-variable
    (= type :type-qname)
    cf-exact-with-variable
    (= type :epsilon)
    cf-epsilon-with-variable
    :default
    (throw (Exception. (str "Unknown constraining function type: " type))))) 

)




  (defn
    to-literal-string
    [value]
    (let [qstr (.toString value)]
      `~qstr))

  (defn 
  ast-primitive-as-expression
  "Returns the string representation of a primitive-valued JDT node (e.g., instances of Modifier.ModifierKeyword)."
  [primitive]
  ;could dispatch on this as well
    (cond (or (true? primitive) (false? primitive))
        primitive
        (number? primitive)
        primitive
        (instance? org.eclipse.jdt.core.dom.Modifier$ModifierKeyword primitive)
        `(~to-modifier-keyword ~(to-literal-string primitive))
        (instance? org.eclipse.jdt.core.dom.PrimitiveType$Code primitive)
        `(~to-primitive-type-code ~(to-literal-string primitive))
        (instance? org.eclipse.jdt.core.dom.Assignment$Operator primitive)
        `(~to-assignment-operator ~(to-literal-string primitive))
        (instance? org.eclipse.jdt.core.dom.InfixExpression$Operator primitive)
        `(~to-infix-expression-operator ~(to-literal-string primitive))
        (instance? org.eclipse.jdt.core.dom.PrefixExpression$Operator primitive)
        `(~to-prefix-expression-operator ~(to-literal-string primitive))
        (instance? org.eclipse.jdt.core.dom.PostfixExpression$Operator primitive)
        `(~to-postfix-expression-operator ~(to-literal-string primitive))
        (nil? primitive) 
        (throw (Exception. (str "Encountered a null-valued property value that should have been wrapped by Ekeko.")))
        :else (to-literal-string primitive)))


  ;;Registering directives
  ;;----------------------

  (def
    directive-exact
    (directives/make-directive
      "match"
      []
      constrain-exact 
      "Type and properties match exactly."))

  (def 
    directive-child
    (directives/make-directive
      "child"
      []
      ground-relativetoparent
      "Child of match for parent."))

  (def 
    directive-offspring
    (directives/make-directive
      "anydepth"
      []
      ground-relativetoparent+ ;arity 0
      "Finds match candidates among the offspring of the match for the parent."))

  (def 
    directive-size|atleast
    (directives/make-directive
      "orlarger"
      []
      constrain-size|atleast 
      "Requires candidate matches to have at least as many elements as the corresponding list in the template."))


  (def 
    directive-replacedbyvariable
    (directives/make-directive
      "replaced-by-variable"
      [(directives/make-directiveoperand "Variable")]
      constrain-replacedbyvariable
      "Node and children have been replaced by a variable."
      ))

  (def 
    directive-equals
    (directives/make-directive
      "equals"
      [(directives/make-directiveoperand "Variable")]
      constrain-equals
      "Match unifies with variable."
      ))


  (def 
    directive-member
    (directives/make-directive
      "anyindex"
      []
      ground-element
      "Match is a member of the match for the parent list."))

  (def 
    directive-replacedbywildcard
    (directives/make-directive
      "replaced-by-wildcard"
      []
      constrain-replacedbywildcard
      "Match is unconstrained."))


  (def 
    directive-consider-as-regexp|lst
    (directives/make-directive
      "regexp|lst"
      []
      constrain-lst|regexp
      "Considers the list as a regexp for element matches."))

  (def
    directive-multiplicity
    (directives/make-directive
      "multiplicity"
      []
      constrain-multiplicity|regexp
      "Determines multiplicity of matches."))

  (def
    directives-constraining|mutuallyexclusive
    [;mutually exclusive ones
     directive-exact
     directive-replacedbyvariable
     directive-replacedbywildcard])


  (def 
    directives-constraining|optional
    [;can be added to the above
     directive-size|atleast
     directive-equals
     directive-consider-as-regexp|lst
     directive-multiplicity
     ])


  (def 
    directives-constraining
    (concat directives-constraining|mutuallyexclusive directives-constraining|optional))


  (def
    directives-grounding
    [directive-child
     directive-offspring
     directive-member
   
     ])
  
  (defn 
    registered-constraining-directives
    "Returns collection of registered constraining directives."
    []
    directives-constraining)

  (defn 
    registered-grounding-directives
    "Returns collection of registered grounding directives."
    []
    directives-grounding)

  (defn
    registered-grounding-directive?
    "Succeeds for registered directives that are grounding."
    [directive]
    (some #{directive} directives-grounding))

  (defn
    registered-constraining-directive?
    "Succeeds for registered directives that are constraining."
    [directive]
    (some #{directive} directives-constraining))


  (defn 
    registered-directives
    []
    (concat (registered-grounding-directives) 
            (registered-constraining-directives)))


  (defn
    registered-directive-for-name
    [name]
    (some
      (fn [directive]
        (when (= name (directives/directive-name directive))
          directive))
      (registered-directives)))



  ;; Auxiliary functions related to regular expression matching
  ;; ---------------------------------------------------------------

  (defn
    snippet-value-regexp-element?
    [snippet value]
    (when
      (astnode/valuelistmember? value)
      (let [owninglst (snippet/snippet-list-containing snippet value)
            owninglstbds (snippet/snippet-bounddirectives-for-node snippet owninglst)]
        (directives/bounddirective-for-directive 
          owninglstbds
          directive-consider-as-regexp|lst))))

  (defn
    snippet-value-multiplicity
    [snippet value]
    (let [bds (snippet/snippet-bounddirectives-for-node snippet value)]
      (if-let [mbd (directives/bounddirective-for-directive 
                     bds
                     directive-multiplicity)]
        (directives/directiveoperandbinding-value 
          (nth (directives/bounddirective-operandbindings mbd) 1))
        "1")))
    


  ;; Constructing Snippet instances with default matching directives
  ;; ---------------------------------------------------------------

  (def default-directives [directive-exact 
                           directive-child 
                           ;ensures these aren't pretty-printed
                           directive-replacedbyvariable 
                           directive-replacedbywildcard
                           ])

  (defn
    default-directive?
    [directive]
    (some #{directive} default-directives))

  (defn
    default-bounddirectives
    "Returns default matching directives for given snippet and element of the snippet element."
    [snippet value]
    (list 
      (directives/bind-directive-with-defaults directive-exact snippet value)
      (directives/bind-directive-with-defaults directive-child snippet value)))

  (defn
    nondefault-bounddirectives
    [snippet value]
    (remove 
      (fn [bounddirective] 
        (default-directive? (directives/bounddirective-directive bounddirective)))
      (snippet/snippet-bounddirectives-for-node snippet value)))

  (defn
    has-nondefault-bounddirectives?
    [snippet value]
    (boolean (not-empty (nondefault-bounddirectives snippet value))))

  (defn
    snippet-nondefault-bounddirectives-string-for-node
    [snippet node]
    (let [bounddirectives (nondefault-bounddirectives snippet node)]
      ;todo: incorporate arguments
      (clojure.string/join " " (map directives/bounddirective-string bounddirectives)))) 

  (defn
    remove-all-directives
    [template value]
    (snippet/update-bounddirectives template value []))

  (defn
    remove-all-directives*
    [template value]
    (let [snippet (atom template)]
      (util/walk-jdt-node 
        value
        (fn [val]
          (swap! snippet remove-all-directives val)))
      @snippet))

  (defn
    remove-all-directives+
    [template value]
    (let [snippet (atom template)]
      (util/walk-jdt-node 
        value
        (fn [val]
          (when (not= val value)
            (swap! snippet remove-all-directives val))))
      @snippet))

  (defn
    remove-directives 
    [template value directives]
    (let [bounddirectives 
          (snippet/snippet-bounddirectives-for-node template value)
          remainingbounddirectives
          (remove (fn [bounddirective]
                    (some #{(directives/bounddirective-directive bounddirective)}
                          directives))
                  bounddirectives)]
      (snippet/update-bounddirectives template value remainingbounddirectives)))

  (defn
    remove-directive
    [template value directive]
    (remove-directives template value [directive]))
  
  (defn 
    remove-value-from-snippet
    "Removes a single value from snippet, does not remove subvalues."
    [s value]
    (->
      (update-in s [:var2ast] dissoc (get-in s [:ast2var value])) 
      (update-in [:ast2var] dissoc value)
      (update-in [:ast2bounddirectives] dissoc value)))

  (defn 
    add-value-to-snippet
    "Adds a single value to snippet, does not add subvalues."
    [snippet value]
    (let [lvar (util/gen-readable-lvar-for-value value)]
      (->
        snippet
        (assoc-in [:ast2var value] lvar)
        (assoc-in [:ast2bounddirectives value] 
                  (default-bounddirectives snippet value))
        (assoc-in [:var2ast lvar] value))))

  (defn 
    jdt-node-as-snippet
    "Interpretes the given JDT ASTNode as a snippet with default matching 
   strategies (i.e., grounding=:exact, constaining=:exact)
   for the values of its properties.
   note: Only used to test operators related binding."
    [n]
    (let [snippet (atom (snippet/make-snippet n))]
      (util/walk-jdt-node 
       n 
       (fn [val] (swap! snippet add-value-to-snippet val)))
      @snippet))

  (defn
    snippet-from-string
    [string]
    (let [parsed (parsing/parse-string-ast string)
          normalized (parsing/parse-string-ast (str parsed))]
      (jdt-node-as-snippet normalized)
    ))


  (defn
    snippet-from-node
    [node]
    (let [string (str node)] ;to normalize char indices to those produced by ast flattener, otherwise persistency fails
      (jdt-node-as-snippet 
        (cond 
          (instance? Expression node)
          (parsing/parse-string-expression string)
          (instance? Statement node)
          (parsing/parse-string-statement string)
          (instance? BodyDeclaration node)
          (parsing/parse-string-declaration string)
          (instance? CompilationUnit node)
          (parsing/parse-string-unit string)
          (instance? ImportDeclaration node)
          (parsing/parse-string-importdeclaration string)
          :default 
          (parsing/parse-string-ast string)))))
    

  (comment
  (defn 
    apply-rewrite 
    "Apply rewrite to snippet."
    [snippet]
    (let [rewrite (snippet/snippet-rewrite snippet)
          document (snippet/snippet-document snippet)]
      (.apply (.rewriteAST rewrite document nil) document)
      (let [newsnippet (document-as-snippet document)]
        (snippet/copy-snippet snippet newsnippet)))) 
)

  (defn
    register-callbacks
    []
    (set! (damp.ekeko.snippets.data.TemplateGroup/FN_SNIPPET_FROM_STRING) snippet-from-string)
    (set! (damp.ekeko.snippets.data.TemplateGroup/FN_SNIPPET_FROM_NODE) snippet-from-node)

    (set! (damp.ekeko.snippets.gui.TemplatePrettyPrinter/FN_SNIPPET_NONDEFAULT_BOUNDDIRECTIVES) nondefault-bounddirectives)
    (set! (damp.ekeko.snippets.gui.TemplatePrettyPrinter/FN_SNIPPET_HAS_NONDEFAULT_BOUNDDIRECTIVES) has-nondefault-bounddirectives?)
  
    (set! (damp.ekeko.snippets.gui.TemplatePrettyPrinter/FN_SNIPPET_BOUNDDIRECTIVES_STRING) snippet-nondefault-bounddirectives-string-for-node)
    (set! (damp.ekeko.snippets.gui.TemplatePrettyPrinter/FN_SNIPPET_USERVAR_FOR_NODE) snippet-replacement-var-for-node)

    (set! (damp.ekeko.snippets.gui.TemplatePrettyPrinter/FN_SNIPPET_ELEMENT_REPLACEDBY_WILDCARD) snippet-node-replaced-by-wilcard?)

  
    (set! (damp.ekeko.snippets.gui.DirectiveSelectionDialog/FN_REGISTERED_DIRECTIVES) registered-directives)
  

    )
  
  (register-callbacks)

  