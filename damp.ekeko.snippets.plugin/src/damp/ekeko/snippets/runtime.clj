(ns 
  ^{:doc "Runtime predicates for snippet-driven querying."
    :author "Coen De Roover, Siltvani"}
  damp.ekeko.snippets.runtime
  (:require [clojure.core.logic :as cl])
  (:require [damp.ekeko [logic :as el]]
            [damp.ekeko.jdt
             [ast :as ast]
             [aststructure :as aststructure]
             [structure :as structure]
             [astbindings :as astbindings]
             [rewrites :as rewrites]
             ;[soot :as soot]
             ]
            [damp.ekeko.snippets 
             [snippetgroup :as snippetgroup]
             ]

            )
  (:import 
    [org.eclipse.jdt.core.dom PrimitiveType Modifier$ModifierKeyword Assignment$Operator
     InfixExpression$Operator PrefixExpression$Operator PostfixExpression$Operator
     SimpleName VariableDeclarationFragment Type MethodDeclaration]
    [damp.ekeko.snippets.gui TemplateCodeGenerator]
    [damp.ekeko.snippets.data TemplateGroup]

    ))

(defn to-primitive-type-code
  [string]
  (PrimitiveType/toCode string)) 

(defn to-modifier-keyword
  [string]
  (Modifier$ModifierKeyword/toKeyword string)) 

(defn to-assignment-operator
  [string]
  (Assignment$Operator/toOperator string)) 

(defn to-infix-expression-operator
  [string]
  (InfixExpression$Operator/toOperator string)) 

(defn to-prefix-expression-operator
  [string]
  (PrefixExpression$Operator/toOperator string)) 

(defn to-postfix-expression-operator
  [string]
  (PostfixExpression$Operator/toOperator string)) 

(defn
  type-relaxmatch-subtype
   "Predicate to check type match ?ltype = ?type or ?ltype = subtype of ?type."
  [?keyword ?type ?ltype]
  (cl/conde [(el/equals ?type ?ltype)]
            [(cl/fresh [?itype ?iltype]
                       (aststructure/ast|type-type ?keyword ?type ?itype)
                       (aststructure/ast|type-type ?keyword ?ltype ?iltype)
                       (structure/type-type|super+ ?iltype ?itype))]))            
             
(defn
  assignment-relaxmatch-variable-declaration
  "Predicate to check relaxmatch of VariableDeclarationStatement (+ initializer) with Assignment."
  [?statement ?left ?right]
  (cl/conde [(cl/fresh [?assignment]
                       (ast/ast :ExpressionStatement ?statement)
                       (ast/has :expression ?statement ?assignment)                        
                       (ast/has :leftHandSide ?assignment ?left)
                       (ast/has :rightHandSide ?assignment ?right))]
            [(cl/fresh [?fragments ?fragmentsraw ?fragment ?fvalue ?avalue ?fname ?aname]
                       (ast/ast :VariableDeclarationStatement ?statement)
                       (ast/has :fragments ?statement ?fragments)
                       (ast/value|list ?fragments)
                       (ast/value-raw ?fragments ?fragmentsraw)
                       (el/equals 1 (.size ?fragmentsraw))
                       (el/equals ?fragment (.get ?fragmentsraw 0))
                       (ast/ast :VariableDeclarationFragment ?fragment)
                       (ast/has :name ?fragment ?left)
                       (ast/has :initializer ?fragment ?right))]))

(defn
  ast-invocation-declaration
   "Relation between ASTNode invocation with it's declaration."
  [?inv ?dec]
  (cl/fresh [?k-inv ?k-dec ?b]
            (astbindings/ast|invocation-binding|method ?k-inv ?inv ?b)
            (astbindings/ast|declaration-binding ?k-dec ?dec ?b)))

(defn
  ast-fieldaccess-samebinding
   "Relation between ASTNode var1 and var2 with the same resolveBinding."
  [?var1 ?var2]
  (cl/fresh [?k-var ?b]
            (astbindings/ast|fieldaccess-binding|variable ?k-var ?var1 ?b)
            (astbindings/ast|fieldaccess-binding|variable ?k-var ?var2 ?b)))

(defn
  ast-fieldaccess-declaration
   "Relation between ASTNode fieldaccess with it's declaration."
  [?var ?dec]
  (cl/fresh [?k-var ?k-dec ?b]
            (astbindings/ast|fieldaccess-binding|variable ?k-var ?var ?b)
            (astbindings/ast|declaration-binding ?k-dec ?dec ?b)))

(defn
  ast-variable-binding
  "Relation between an variable (SimpleName) instance ?ast,
   the keyword ?key representing its kind,
   and the IBinding ?binding for its type."
  [?key ?ast ?binding]
  (cl/all
    (ast/ast :SimpleName ?ast)
    (el/equals ?binding (.resolveBinding ^SimpleName ?ast))
    (cl/!= nil ?binding)
    (ast/ast ?key ?ast)))

(defn
  ast-variable-declaration-binding
  "Relation between a simple name ?ast (reside in variable declaration fragment),
   the keyword ?key representing its kind,
   and the IBinding ?binding for its type."
  [?key ?ast ?binding]
  (cl/fresh [?fragment]
    (ast/ast :SimpleName ?ast)
    (el/equals ?fragment (.getParent ?ast))
    (ast/ast :VariableDeclarationFragment ?fragment)
    (el/equals ?binding (.resolveBinding ^VariableDeclarationFragment ?fragment))
    (cl/!= nil ?binding)
    (ast/ast ?key ?ast)))

(defn
  ast-variable-samebinding
   "Relation between ASTNode var1 and var2 with the same resolveBinding."
  [?var1 ?var2]
  (cl/fresh [?k-var ?b]
            (ast-variable-binding ?k-var ?var1 ?b)
            (ast-variable-binding ?k-var ?var2 ?b)))

(defn
  ast-variable-declaration
   "Relation between ASTNode variable with it's declaration."
  [?var ?dec]
  (cl/fresh [?k-var ?k-dec ?b]
            (ast-variable-binding ?k-var ?var ?b)
            (ast-variable-declaration-binding ?k-dec ?dec ?b)))

;(defn
;  ast-may-alias
;   "Relation between ASTNode ast1 may alias ast2."
;  [?ast1 ?ast2]
;  (cl/fresh [?model]
;            (soot/ast-references-soot-model-may-alias ?ast1 ?ast2 ?model)))
  
(defn
  ast-variable-sameidentifier
   "Relation between ASTNode var1 and var2 with the same identifier."
  [?var1 ?var2]
  (cl/fresh [?id1 ?id2 ?value]
            (ast/ast :SimpleName ?var1)
            (ast/ast :SimpleName ?var2)
            (ast/has :identifier ?var1 ?id1) 
            (ast/has :identifier ?var2 ?id2)
            (ast/value-raw ?id1 ?value) 
            (ast/value-raw ?id2 ?value))) 

(defn
  ast-samekind-sameidentifier
   "Relation between ASTNode var1 and var2 with the same identifier."
  [?var1 ?var2]
      (cl/fresh [?key ?key-id ?var1-id ?var2-id  ?identifier]
                (ast/ast ?key ?var1)
                (ast/ast ?key ?var2)
                (ast/has ?key-id ?var1 ?var1-id)
                (ast/has ?key-id ?var2 ?var2-id)
                (ast/value-raw ?var1-id ?identifier)
                (ast/value-raw ?var2-id ?identifier)))

(defn
  ast-variable-type
   "Relation between ASTNode var with it's type."
  [?var ?type]
  (cl/conde [(cl/fresh [?dec ?stat ?frag]
                       (ast-variable-declaration ?var ?dec)
                       (ast/ast :SimpleName ?dec)
                       (el/equals ?frag (.getParent ?dec))
                       (ast/ast :VariableDeclarationFragment ?frag)
                       (el/equals ?stat (.getParent ?frag))
                       (ast/ast :VariableDeclarationStatement ?stat)
                       (ast/has :type ?stat ?type))]
            [(cl/fresh [?dec ?stat ?frag]
                       (ast-variable-declaration ?var ?dec)
                       (ast/ast :SimpleName ?dec)
                       (el/equals ?frag (.getParent ?dec))
                       (ast/ast :VariableDeclarationFragment ?frag)
                       (el/equals ?stat (.getParent ?frag))
                       (ast/ast :FieldDeclaration ?stat)
                       (ast/has :type ?stat ?type))]))
                                    
(defn 
  ast-type-qualifiednamestring
   "Relation between ASTNode var with it's qualified name (in string)."
  [?type ?string]
  (cl/fresh [?binding]
            (ast/ast :Type ?type)
            (el/equals ?binding (.resolveBinding ^Type ?type))
            (cl/!= nil ?binding)
            (el/equals ?string (.getQualifiedName ?binding))))

(defn 
  ast-type-qualifiednamecontain
   "Relation between ASTNode var with it's qualified name (contain the string)."
  [?type ?string]
  (cl/fresh [?qname]
            (ast-type-qualifiednamestring ?type ?qname)
            (el/succeeds (.contains ?qname ?string))))

(defn 
  ast-type-qualifiedname
   "Relation between ASTNode var with it's qualified name."
  [?type ?qname]
  (cl/fresh [?string] 
            (ast-type-qualifiednamestring ?type ?string)
            (ast/ast :QualifiedName ?qname)
            (el/succeeds (.contains ?string (str ?qname)))))
  
(defn
  ast-variable-typequalifiednamestring
   "Relation between ASTNode var with it's type qualified name (in string)."
  [?var ?string]
  (cl/fresh [?type]
            (ast-variable-type ?var ?type)
            (ast-type-qualifiednamecontain ?type ?string)))

(defn
  ast-variable-typequalifiedname
   "Relation between ASTNode var with it's type qualified name."
  [?var ?qname]
  (cl/fresh [?type]
            (ast-variable-type ?var ?type)
            (ast-type-qualifiedname ?type ?qname)))

    
(defn
  match-match|samehierarchy
  [?a ?b]
  (cl/fresh [?root]
         (ast/astorvalue-root ?a ?root)
         (ast/astorvalue-root ?b ?root)))




  ;; Checked
  ;; -------

(comment
  (defn
    ground-relativetoparent+|match-ownermatch-userarg 
    "Used by matching directive parent+/1 (ground-relativetoparent+)."
    [?match ?ownermatch ?userarg]
    (cl/conda [(el/v+ ?userarg)
               ;?ownermatch and ?userarg both ground
               (match-match|samehierarchy ?ownermatch ?userarg) ;short-circuits other hierarchies
               (ast/astorvalue-offspring+ ?userarg ?match)]
              [(el/v- ?userarg)
               (cl/fresh [?root]
                      (ast/ast-root ?ownermatch ?root)
                      (ast/astorvalue-offspring+ ?root ?match))]))
)

(defn 
  template-to-string|projected
  [template variables values]
  (let [var2value
        (zipmap variables values)
        templategroup
        (snippetgroup/make-snippetgroup "Dummy group" [template])
        jtemplategroup
        (TemplateGroup/newFromClojureGroup templategroup)
        generator 
        (TemplateCodeGenerator. jtemplategroup var2value)]
    (.prettyPrintSnippet generator template)))
    
    
        

  

