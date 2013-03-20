(ns 
  ^{:doc "Runtime predicates for snippet-driven querying."
    :author "Coen De Roover, Siltvani"}
  damp.ekeko.snippets.runtime
  (:require [clojure.core.logic :as cl])
  (:require [damp.ekeko [logic :as el]]
            [damp.ekeko.jdt
             [basic :as basic]
             [reification :as reification]
             [soot :as soot]])
  (:import 
    [org.eclipse.jdt.core.dom PrimitiveType Modifier$ModifierKeyword Assignment$Operator
     InfixExpression$Operator PrefixExpression$Operator SimpleName VariableDeclarationFragment]))

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

(defn
  type-relaxmatch-subtype
   "Predicate to check type match ?ltype = ?type or ?ltype = subtype of ?type."
  [?keyword ?type ?ltype]
  (cl/conde [(el/equals ?type ?ltype)]
            [(cl/fresh [?itype ?iltype]
                       (reification/ast-type-type ?keyword ?type ?itype)
                       (reification/ast-type-type ?keyword ?ltype ?iltype)
                       (reification/type-super-type ?iltype ?itype))]))            
             
(defn
  assignment-relaxmatch-variable-declaration
  "Predicate to check relaxmatch of VariableDeclarationStatement (+ initializer) with Assignment."
  [?statement ?left ?right]
  (cl/conde [(cl/fresh [?assignment]
                       (reification/ast :ExpressionStatement ?statement)
                       (reification/has :expression ?statement ?assignment)                        
                       (reification/has :leftHandSide ?assignment ?left)
                       (reification/has :rightHandSide ?assignment ?right))]
            [(cl/fresh [?fragments ?fragmentsraw ?fragment ?fvalue ?avalue ?fname ?aname]
                       (reification/ast :VariableDeclarationStatement ?statement)
                       (reification/has :fragments ?statement ?fragments)
                       (reification/listvalue ?fragments)
                       (reification/value-raw ?fragments ?fragmentsraw)
                       (el/equals 1 (.size ?fragmentsraw))
                       (el/equals ?fragment (.get ?fragmentsraw 0))
                       (reification/ast :VariableDeclarationFragment ?fragment)
                       (reification/has :name ?fragment ?left)
                       (reification/has :initializer ?fragment ?right))]))

(defn
  ast-invocation-declaration
   "Relation between ASTNode invocation with it's declaration."
  [?inv ?dec]
  (cl/fresh [?k-inv ?k-dec ?b]
            (reification/ast-invocation-binding ?k-inv ?inv ?b)
            (reification/ast-declares-binding ?k-dec ?dec ?b)))

(defn
  ast-fieldaccess-samebinding
   "Relation between ASTNode var1 and var2 with the same resolveBinding."
  [?var1 ?var2]
  (cl/fresh [?k-var ?b]
            (reification/ast-fieldaccess-binding ?k-var ?var1 ?b)
            (reification/ast-fieldaccess-binding ?k-var ?var2 ?b)))

(defn
  ast-fieldaccess-declaration
   "Relation between ASTNode fieldaccess with it's declaration."
  [?var ?dec]
  (cl/fresh [?k-var ?k-dec ?b]
            (reification/ast-fieldaccess-binding ?k-var ?var ?b)
            (reification/ast-declares-binding ?k-dec ?dec ?b)))

(defn
  ast-variable-binding
  "Relation between an variable (SimpleName) instance ?ast,
   the keyword ?key representing its kind,
   and the IBinding ?binding for its type."
  [?key ?ast ?binding]
  (cl/all
    (reification/ast :SimpleName ?ast)
    (el/equals ?binding (.resolveBinding ^SimpleName ?ast))
    (cl/!= nil ?binding)
    (reification/ast ?key ?ast)))

(defn
  ast-variable-declaration-binding
  "Relation between an variable declaration fragment instance ?ast,
   the keyword ?key representing its kind,
   and the IBinding ?binding for its type."
  [?key ?ast ?binding]
  (cl/all
    (reification/ast :VariableDeclarationFragment ?ast)
    (el/equals ?binding (.resolveBinding ^VariableDeclarationFragment ?ast))
    (cl/!= nil ?binding)
    (reification/ast ?key ?ast)))

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

(defn
  ast-may-alias
   "Relation between ASTNode ast1 may alias ast2."
  [?ast1 ?ast2]
  (cl/fresh [?model]
            (soot/ast-references-soot-model-may-alias ?ast1 ?ast2 ?model)))
  
