(ns lime.ray-test
  (:require [clojure.test :refer :all]
            [lime.ray :refer :all])
  (:import (lime.materials Matte Phong)
           (lime.light Lambertian GlossySpecular PointLight Ambient)
           (lime.tracers Raycast)
           (lime.sphere Sphere)))

(def sphere (Sphere.
              [0.0 85.0 -30.0] ; center
              85.0 ; radius
              (Phong. (Lambertian. 0.25 [0.749 1.0 0.0])
                      (Lambertian. 0.65 [0.749 1.0 0.0])
                      (GlossySpecular. 0.5 100))))


(def pointlight (PointLight. 4 [1.0 1.0 1.0] [70.0 110.0 150.0]))

(deftest hit-sphere-when-should
  (is (not (nil?
                 (hit {:origin [0.0 0.0 0.0]
                       :direction [0.0 0.0 -1.0]
                       :parameter 0.0}
                      sphere))))
  "A ray shot on a sphere returns a non nil hit-location")


(deftest miss-sphere-when-should
  (is (nil?
           (hit {:origin [-384.0 0.0 0.0]
                 :direction [0.0 0.0 -1.0]
                 :parameter 0.0}
                sphere)))
  "A ray missing a sphere returns nil")

(deftest trace-nil-when-miss
  (is (nil? (second (trace {:tracer (Raycast.) 
                               :ambient (Ambient. 1 [0.5 0.5 0.5])
                               :camera {:type "pinhole"
                                        :eye [0.0 400.0 500.0]
                                        :look-at [0.0 0.0 -100.0]
                                        :up [0.0 1.0 0.0]
                                        :distance 100
                                        :zoom 5}
                               :view {:default-colour [0.0 0.0 0.0]}
                               :scene {:objects [sphere]
                                       :lights [pointlight]}}
                               [-384.0 0.0 0.0]))))
      "Default colour is used when there are no hits")

(deftest trace-non-nil-when-hit
  (is (not (nil? (second (trace {:tracer (Raycast.)
                                  :ambient (Ambient. 1 [0.5 0.5 0.5])
                                  :camera {:type "pinhole"
                                        :eye [0.0 400.0 500.0]
                                        :look-at [0.0 0.0 -100.0]
                                        :up [0.0 1.0 0.0]
                                        :distance 100
                                        :zoom 5}
                                  :view {:default-colour [0.0 0.0 0.0]}
                                   :scene {:objects [sphere] 
                                           :lights [pointlight]}}
                                 [0.0 0.0 0.0])))))
      "Shaded colour is used when there are hits")
