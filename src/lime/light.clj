;; ## Lighting
;; To better reflect (pun intented) how lighting works in the real world, lighting calculations are split in two parts: 
;;
;;        - BRDFs
;;        - Light sources themselves
;; BRDFs indicate the ratio of emitted radiance at a given point, and light sources indicate how much incident radiance can possibly hit any given point.
;; To keep later calculations as generic as possible, `lime` defines a protocol for all BRDFs as well as one for Lights. As of today, only Lambertian, Diffuse and Glossy Specular are available as BRDF, and only Point Light and Ambient are defined as Lights. More will be added later to both categories.
(ns lime.light
  (:require [lime.vectors :as vectors]
            [lime.ray :as ray]
            [lime.sanitise :refer :all])
  (:import (java.lang Math)))

(defprotocol BRDF
  "Bidirectional Reflectance Distribution Function protocol."
  (f [this intersection wo wi] "f function")
  (sample-f [this] "sample-f function")
  (rho [this intersection wo] "rho function"))

;; Lambertian BRDF, also often refered to as 'Flat shading'.
(defrecord Lambertian [kd cd]
  BRDF
  (f [this intersection wo wi]
    (vectors/scalar / (java.lang.Math/PI) (vectors/scalar * (:kd this) (:cd this))))
  (sample-f [this]
    (println "Not implemented yet"))
  (rho [this intersection wo]
    (vectors/scalar * (:kd this) (:cd this))))

(defn make-lambertian
  [{:keys [kd cd]}]
  {:pre [(number? kd)
         (pos? kd)
         (in-gamut? cd)]}
    (Lambertian. kd cd))

;; The GlossySpecular BRDF is a viewer-dependant BRDF.
(defrecord GlossySpecular [ks exp]
  BRDF
  (f [this intersection wo wi]
    (let [ndotwi (vectors/dot (:normal intersection) wi)
          r (map + (map - wi)
                 (vectors/scalar * 2
                    (vectors/scalar * ndotwi (:normal intersection))))
                    ;(vectors/scalar * ndotwi wi)))
          rdotwo (vectors/dot r wo)]
      (if (pos? rdotwo)
        (repeat 3 (* (:ks this) (java.lang.Math/pow rdotwo exp)))
        (repeat 3 0.0))))
  (sample-f [this]
    (println "Not implemented yet."))
  (rho [this intersection wo]
    (println "Not implemented yet.")))

(defn make-glossy-specular
  [{:keys [ks exp]}]
  {:pre [(number? ks)
         (pos? ks)
         (<= ks 1.0)]}
  (GlossySpecular. ks exp))

(defprotocol Light
  "Protocol for all kinds of light."
  (direction [this location] "Returns the vector from location to light.")
  (L [this intersection] "Returns the incident radiance at the location.")
  (in-shadow? [this ray objects]))

;; The Ambient light is a very complex light to replicate entirely. The Ambient light defined in lime is only a very crude approximation of the reality (as in most ray tracers).
;; Ambient lights have two members: `ls` is the intensity of the incident light and `colour` is the actual colour of the light.
;;
(defrecord Ambient [ls colour]
  Light
  (direction [this location] [0.0 0.0 0.0])
  (L
    [this intersection]
    (vectors/scalar * (:ls this) (:colour this)))
  (in-shadow? [this ray objects]
    false))

(defn make-ambient
  [{:keys [ls colour]}]
  {:pre [(in-gamut? colour)]}
  (Ambient. ls colour))

;; A PointLight has no reality, it is just a very handy abstraction. Here, it is a light emitted at a specific point in space, defined with: `ls` the intensity of the light, `colour` which is the colour of the light and `location`, where in space the PointLight resides.
(defrecord PointLight [ls colour location]
  Light
  (direction
    [this location]
    (vectors/normal (map - (:location this) location)))
  (L
    [this intersection]
    (vectors/scalar * (:ls this) (:colour this)))
  (in-shadow?
    [this ray objects]
    (let [[x1 y1 z1] (:location this)
          [x2 y2 z2] (:origin ray)
          distance (Math/sqrt (+ (Math/pow (- x1 x2) 2)
                                 (Math/pow (- y1 y2) 2)
                                 (Math/pow (- z1 z2) 2)))]
      (some true? (map #(let [t (:tmin (ray/hit ray %))]
                             (and (identity t)
                                 (< t distance))) objects)))))

(defn make-point-light
  [{:keys [ls colour location]}]
  {:pre [(in-gamut? colour)]}
  (PointLight. ls colour location))

;; A Directional light can be seen as an oriented Point light. Light is not emitted in all directions at the same time, but only along a specific vector. This means that a Directional light has three slots: `ls` is the intensity of the light, `colour` is the colour of the light and `dir` is the direction (vector) of the light.
(defrecord Directional [ls colour dir]
  Light
  (direction [this location] (map - (vectors/normal (:dir this))))
  (L
    [this intersection]
    (vectors/scalar * (:ls this) (:colour this)))
  (in-shadow?
    [this ray objects]
    false))

(defn make-directional-light
  [{:keys [ls colour direction]}]
  {:pre [(in-gamut? colour)]}
  (Directional. ls colour direction))
