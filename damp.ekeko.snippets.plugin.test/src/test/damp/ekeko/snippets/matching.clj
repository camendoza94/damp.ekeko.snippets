(ns 
  ^{:doc "Test suite for persistence of snippets."
    :author "Coen De Roover"}
  test.damp.ekeko.snippets.matching
  (:refer-clojure :exclude [== type declare record?])
  (:require  [clojure.core.logic :exclude [is] :as l])
  (:require [damp.ekeko.snippets 
             [persistence :as persistence]
             ])
  (:require [test.damp [ekeko :as test]])
  (:require [damp.ekeko.jdt 
             [javaprojectmodel :as javaprojectmodel]
              ])
  (:use clojure.test))




;; Persisting JDT AST nodes
;; -----------------------------------

(deftest
  ^{:doc "For all nodes compilation units cu, cu has to be persistable."}
   persist-compilationunits
  (let [cus
        (mapcat (fn [jpm] (.getCompilationUnits jpm))
                (javaprojectmodel/java-project-models))]
    (doseq [cu cus]
      (let [serialized (persistence/snippet-as-persistent-string cu)
            deserialized (persistence/snippet-from-persistent-string serialized)]
        (is (= (class cu)
               (class deserialized)))))))
      
;; Test suite
  ;; ----------

(deftest
   test-suite 
   (let [testproject "TestCase-JDT-CompositeVisitor"]
     (test/against-project-named testproject false persist-compilationunits)
     )
   )

(defn 
  test-ns-hook 
  []
  (test/with-ekeko-disabled test-suite))


(comment  
  ;;Example repl session 
  (run-tests)
  )
  

