(ns lime.sphere-test
  (:require [clojure.test :refer :all]
             [lime.sphere :refer :all])
  (:import (lime.sphere Sphere)
           (lime.materials Matte Phong)
           (lime.light Lambertian GlossySpecular PointLight Ambient)
           (lime.tracers Raycast)))
; some data
(def sphere (make-sphere [0.0 85.0 -30.0] ; center
              85.0 ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100))))

; sphere definition tests

(deftest radius-neg
  (is (thrown? AssertionError (make-sphere
              [0.0 85.0 -30.0] ; center
              -3.0 ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100)))))
  "The radius of a sphere must be a positive number")

(deftest radius-zero
  (is (thrown? AssertionError (make-sphere
              [0.0 85.0 -30.0] ; center
              0.0 ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100)))))
  "The radius of a sphere must be a positive number")

(deftest radius-nil
  (is (thrown? AssertionError (make-sphere
              [0.0 85.0 -30.0] ; center
              nil ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100)))))
  "The radius of a sphere must be a positive number")

(deftest radius-alpha
  (is (thrown? AssertionError (make-sphere
              [0.0 85.0 -30.0] ; center
              "wrong data" ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100)))))
  "The radius of a sphere must be a positive number")

; hit tests

(deftest hit-when-should
  (is (not (nil?
                 (.hit sphere
                       {:origin [0.0 0.0 0.0]
                       :direction [0.0 0.0 -1.0]
                       :parameter 0.0}))))
  "A ray shot on a sphere returns a non nil hit-location")

(deftest no-hit-when-miss
  (is (nil?
        (.hit sphere
              {:origin [-384.0 0.0 0.0]
              :direction [0.0 0.0 -1.0]
              :parameter 0.0})))
  "A ray missing a sphere returns nil")

