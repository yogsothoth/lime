;; ## Materials
;; Object materials are greatly responsible for how visible an object is and how much of the incident light is emitted back.
;; Materials are modeled around a dedicated protocol, declaring functions for four different approaches to shading. As of today, only the `shade` function is actually implemented, for traditional ray casting. Two materials are defined: `Matte` and `Phong`, the former relying on two  `Lambertian` BRDFs for ambient and diffuse radiance and the latter adding a `GlossySpecular` BRDF for specular highlight.

(ns lime.materials
    (:require [lime.vectors :as vectors]
              [lime.debug :as dbg])
    (:import (lime.light Ambient PointLight BRDF)))

(defprotocol Material
    "Protocol for all shading material."
    (shade [this ray intersection world])
    (whitted-shade [this])
    (area-light-shade [this])
    (path-shade [this]))
;; The Matte material renders only ambient and diffuse components of the lighting equation.
(defrecord Matte [ambient-brdf diffuse-brdf]
    Material
    (shade
        [this ray intersection world]
        (let [wo (map - (:direction ray))
              L (map * (.rho ambient-brdf intersection wo)
                     (.L (:ambient world) intersection))]
          (map + L (apply map + 
               (map (fn [light]
                 (let [wi (.direction light (:hit-point intersection))
                       ndotwi (vectors/dot (:normal intersection) wi)
                       shadow-ray {:origin (:hit-point intersection)
                                   :direction wi
                                   :parameter nil}
                       objects (:objects (:scene world))]
                       (if (and (pos? ndotwi)
                                (not (.in-shadow? light shadow-ray objects)))
                         (vectors/scalar * ndotwi 
                             (map * 
                              (.f diffuse-brdf intersection wo wi)
                              (.L light intersection)))
                         [0.0 0.0 0.0]))) ((:scene world) :lights))))))
    (whitted-shade [this] (println "Not implemented"))
    (area-light-shade [this] (println "Not implemented"))
    (path-shade [this] (println "Not implemented")))

(defn make-matte
  [{:keys [ambient-brdf diffuse-brdf]}]
  {:pre [(instance? BRDF ambient-brdf)
         (instance? BRDF diffuse-brdf)]}
  (Matte. ambient-brdf diffuse-brdf))

;; The Phong material builds on the Matte material (see above) and adds specular highlight, for shiny objects.
(defrecord Phong [ambient-brdf diffuse-brdf specular-brdf]
  Material
  (shade
    [this ray intersection world]
        (let [wo (map - (:direction ray))
              L (map * (.rho ambient-brdf intersection wo)
                     (.L (:ambient world) intersection))]
          (map + L (apply map + 
               (map (fn [light]
                 (let [wi (.direction light (:hit-point intersection))
                       ndotwi (vectors/dot (:normal intersection) wi)
                       shadow-ray {:origin (:hit-point intersection)
                                   :direction wi
                                   :parameter nil}
                       objects (:objects (:scene world))]
                       (if (and (pos? ndotwi)
                                (not (.in-shadow? light shadow-ray objects)))
                         (vectors/scalar * ndotwi 
                             (map * 
                              (map + 
                                (.f diffuse-brdf intersection wo wi)
                                (.f specular-brdf intersection wo wi))
                              (.L light intersection)))
                         [0.0 0.0 0.0]))) ((:scene world) :lights))))))
    (whitted-shade [this] (println "Not implemented"))
    (area-light-shade [this] (println "Not implemented"))
    (path-shade [this] (println "Not implemented")))

(defn make-phong
  [{:keys [ambient-brdf diffuse-brdf specular-brdf]}]
  {:pre [(instance? BRDF ambient-brdf)
         (instance? BRDF diffuse-brdf)
         (instance? BRDF specular-brdf)]}
  (Phong. ambient-brdf diffuse-brdf specular-brdf))
