(ns lime.light-test
  (:require [clojure.test :refer :all]
            [lime.light :refer :all]
            [lime.debug :as dbg])
  (:import (lime.light Lambertian GlossySpecular PointLight Ambient Directional)))

; Lambertian tests

(deftest lambertian-kd-zero
  (is (thrown? AssertionError
               (make-lambertian {:kd 0.0 :cd [0.0 0.0 0.0]})))
  "kd must be a positive number for a Lambertian BRDF")

(deftest lambertian-kd-negative
  (is (thrown? AssertionError
               (make-lambertian {:kd -1.0 :cd [0.0 0.0 0.0]})))
  "kd must be a positive number for a Lambertian BRDF")

(deftest lambertian-cd-negative1
  (is (thrown? AssertionError
               (make-lambertian {:kd 1.0 :cd [-1.0 0.0 0.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-cd-negative2
  (is (thrown? AssertionError
               (make-lambertian {:kd 1.0 :cd [1.0 -1.0 0.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-cd-negative3
  (is (thrown? AssertionError
              (make-lambertian {:kd 1.0 :cd [1.0 0.0 -1.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-cd-too-big1
  (is (thrown? AssertionError
              (make-lambertian {:kd 1.0 :cd [2.0 0.0 0.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-cd-too-big2
  (is (thrown? AssertionError
              (make-lambertian {:kd 1.0 :cd [0.0 2.0 0.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-cd-too-big3
  (is (thrown? AssertionError
              (make-lambertian {:kd 1.0 :cd [0.0 0.0 2.0]})))
  "All cd components must be between 0.0 and 1.0 for a Lambertian BRDF")

(deftest lambertian-correct
  (is (= (Lambertian. 1.0 [1.0 0.0 0.5])
         (make-lambertian {:kd 1.0 :cd [1.0 0.0 0.5]})))
  "Lambertian positive test")

; GlossySpecular

(deftest glossy-ks-zero
  (is (thrown? AssertionError
               (make-glossy-specular {:ks 0.0 :exp 1.0})))
  "ks must be a positive number for a GlossySpecular BRDF")


(deftest glossy-ks-negative
  (is (thrown? AssertionError
               (make-glossy-specular {:ks -1.0 :exp 1.0})))
  "ks must be a positive number for a GlossySpecular BRDF")

(deftest glossy-ks-too-big
  (is (thrown? AssertionError
               (make-glossy-specular {:ks 2.0 :exp 1.0})))
  "ks must be a inferior to 1.0 for a GlossySpecular BRDF")


; Ambient light

(deftest ambient-colour-neg1
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [-1.0 0.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-neg2
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [0.0 -1.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-neg3
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [0.0 0.0 -1.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-too-big1
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [2.0 0.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-too-big2
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [0.0 2.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-too-big3
  (is (thrown? AssertionError
               (make-ambient {:ls 1.0 :colour [0.0 0.0 3.0]})))
  "All colour components must be between 0.0 and 1.0 for an Ambient light")

(deftest ambient-colour-correct
  (is (= (Ambient. 1.0 [1.0 1.0 1.0])
               (make-ambient {:ls 1.0 :colour [1.0 1.0 1.0]})))
  "Ambient light positive test")


; Point light

(deftest point-light-colour-neg1
  (is (thrown? AssertionError
               (make-point-light {:ls 1.0 :colour [-1.0 0.0 0.0] :location [0.0 0.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for a PointLight")

(deftest point-light-colour-neg2
  (is (thrown? AssertionError
               (make-point-light {:ls 1.0 :colour [0.0 -1.0 0.0] :location [0.0 0.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for a PointLight")

(deftest point-light-colour-neg3
  (is (thrown? AssertionError
               (make-point-light {:ls 1.0 :colour [0.0 0.0 -1.0] :location [0.0 0.0 0.0]})))
  "All colour components must be between 0.0 and 1.0 for a PointLight")

(deftest point-light-correct
  (is (= (PointLight. 1.0 [1.0 1.0 1.0] [0.0 0.0 0.0])
         (make-point-light {:ls 1.0 :colour [1.0 1.0 1.0] :location [0.0 0.0 0.0]})))
  "PointLight positive test")

; Directional light

(deftest directional-colour-neg1
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [-1.0 0.0 0.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")


(deftest directional-colour-neg2
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [0.0 -1.0 0.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")

(deftest directional-colour-neg3
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [0.0 0.0 -1.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")

(deftest directional-colour-too-big1
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [2.0 0.0 0.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")

(deftest directional-colour-too-big2
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [0.0 2.0 0.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")

(deftest directional-colour-too-big3
  (is (thrown? AssertionError
               (make-directional-light {:ls 1.0 :colour [0.0 0.0 2.0] :direction [1.0 1.0 1.0]})))
  "All colour components must be between 0.0 and 1.0 for a DirectionalLight")

(deftest directional-colour-too-big3
  (is (= (Directional. 1.0 [1.0 1.0 1.0] [1.0 1.0 1.0])
               (make-directional-light {:ls 1.0 :colour [1.0 1.0 1.0] :direction [1.0 1.0 1.0]})))
  "Positive DirectionalLight test")

