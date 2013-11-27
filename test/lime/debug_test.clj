(ns lime.debug-test
  (:require [clojure.test :refer :all]
            [lime.debug :refer :all]))

(deftest debug-eval-numbers
  (is (=
       2 (dbg 2)))
  "dbg returns numbers untouched.")

(deftest debug-eval-strings
  (is (=
       "Test string" (dbg "Test string")))
  "dbg returns strings untouched.")


(deftest debug-eval-lists
  (is (=
       '(println "Should not print") (dbg '(println "Should not print"))))
  "dbg returns lists untouched and unevaluated.")
