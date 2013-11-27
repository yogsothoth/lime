(ns lime.materials-test
  (:require [clojure.test :refer :all]
            [lime.materials :refer :all]
            [lime.light :refer :all]
            [lime.debug :as dbg])
  (:import (lime.materials Matte Phong)
           (lime.light Lambertian GlossySpecular)))

; Matte

(deftest matte-correct
  (is (= (Matte. (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                 (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]}))
         (make-matte
                 {:ambient-brdf (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                  :diffuse-brdf (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})})))
  "Matte positive test")

; Phong

(deftest phong-correct
  (is (= (Phong. (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                 (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                 (make-glossy-specular {:ks 1.0 :exp 1.0}))
         (make-phong
                 {:ambient-brdf (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                  :diffuse-brdf (make-lambertian {:kd 1.0 :cd [1.0 1.0 1.0]})
                  :specular-brdf (make-glossy-specular {:ks 1.0 :exp 1.0})})))
  "Phong positive test")

