(ns 
  ^{:doc "Test suite for snippet-driven querying of Java projects."
    :author "Siltvani, Coen De Roover"}
  test.damp.ekeko.snippets
  (:refer-clojure :exclude [== type declare])
  (:use [clojure.core.logic :exclude [is]] :reload)
  (:use [damp.ekeko logic])
  (:use clojure.test)
  (:require [test.damp [ekeko :as test]]
            [damp.ekeko.jdt [reification :as reification]]
            [damp.ekeko [snippets :as snippets]]))

;; General queries
(defn query-for-get-methods [methods]
  "methods -> vector of methods name eg. [\"methodA\" \"methodA1\"]"
          (damp.ekeko/ekeko [?m] 
                    (reification/ast :MethodDeclaration ?m) 
                    (fresh [?n ?id]
                           (reification/has :name ?m ?n)
                           (reification/has :identifier ?n ?id)
                           (contains methods ?id))))

;; Individual tests

(deftest
  invocationsnippet-exactmatch-test
  (test/tuples-are 
    (snippets/query-by-snippet (snippets/jdt-node-as-snippet (snippets/parse-string-expression "x.m()")))
    "#{(\"x.m()\")}")) ;string obtained by evaluating (test/tuples-to-stringsetstring (snippets/query-by-snippet .....
     

;; Project tests

;; Snippet = methodA
(defn selected-snippet []
  (first (first (query-for-get-methods ["methodA"]))))
        
;; Test - Node exact match
;; against methodA

;; test
(deftest
  node-exactmatch-test
    (test/tuples-are 
      (snippets/query-by-snippet (snippets/jdt-node-as-snippet (selected-snippet)))
      "#{(\"public void methodA(){\\n  this.methodM();\\n  this.methodC();\\n}\\n\")}"))

;; Test - Introduce logic variable 
;; against methodA & methodA1

;; Node for a SimpleName of methodA
(defn selected-node [?m]
  (first (first 
           (damp.ekeko/ekeko [?s]
                           (reification/has :name ?m ?s)
                           (reification/has :identifier ?s "methodA")))))

;; unit test
(defn introduce-logic-variable-unittest []
  (let [selected    (selected-snippet)
        snippet     (snippets/jdt-node-as-snippet selected)
        newsnippet  (snippets/introduce-logic-variable snippet (selected-node selected) '?m)]
    (snippets/query-by-snippet newsnippet)))

;; test
(deftest
  introduce-logic-variable-test
    (test/tuples-are 
      (introduce-logic-variable-unittest)
      (test/tuples-to-stringsetstring 
          (query-for-get-methods ["methodA" "methodA1"]))))

;; Test - List contains match
;; against methodA, methodA1 & methodA2

;; list (statements) of methodA
(defn selected-list [?m]
  (first (first 
           (damp.ekeko/ekeko [?l]
                    (fresh [?b]
                           (reification/has :body ?m ?b)
                           (reification/has :statements ?b ?l))))))

;; unit test
(defn list-contains-unittest []
  (let [selected    (selected-snippet)
        snippet     (snippets/jdt-node-as-snippet selected)
        newsnippet  (snippets/introduce-logic-variable snippet (selected-node selected) '?m)
        newsnippet  (snippets/ignore-elements-sequence newsnippet (selected-list selected))]
    (snippets/query-by-snippet newsnippet)))

;; test
(deftest
  list-contains-test
    (test/tuples-are 
      (list-contains-unittest)
      (test/tuples-to-stringsetstring 
          (query-for-get-methods ["methodA" "methodA1" "methodA2"]))))

;; Test suite

(deftest
   test-suite 
   ;(test/against-project-named "TestCase-Snippets-BasicMatching" false invocationsnippet-exactmatch-test)
   (test/against-project-named "TestCase-Snippets-BasicMatching" false node-exactmatch-test)
   ;(test/against-project-named "TestCase-Snippets-BasicMatching" false introduce-logic-variable-test)
   ;(test/against-project-named "TestCase-Snippets-BasicMatching" false list-contains-test)
   )

(defn 
  test-ns-hook 
  []
  (test/with-ekeko-disabled test-suite))


(comment  
  ;;Example repl session 
  (run-tests)
  )
  
