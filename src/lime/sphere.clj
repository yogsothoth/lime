(ns lime.sphere 
  (:require [lime.vectors :as vectors]
            [lime.shapes :refer [Shape]]))


;;Sphere implementation of the Shape protocol.
(defrecord Sphere [center radius material]
  Shape
  (hit [sphere ray]
  "Computes the intersection between a sphere and a ray.
  Returns an intersection as a hashmap containing the following data:
  
        :object     the current object
        :tmin       the min t value for the hit
        :normal     the normal at the hit point
        :hit-point  xyz triplet of the hit point."
  (let [ 
        o (:origin ray)
        t (:parameter ray)
        d (:direction ray)
        c (:center sphere)
        r (:radius sphere)
        A (vectors/dot d d)
        B (* 2 (vectors/dot (map - o c) d))
        C (- (vectors/dot (map - o c) (map - o c)) (* r r))
        disc (- (* B B) (* 4  A C))]
        (when-not (neg? disc)
          (let [
                e (Math/sqrt disc)
                denom (* 2.0 A)
                smaller_root (/ (- (- B) e) denom)
                larger_root (/ (+ (- B) e) denom)]
                (if (> smaller_root 0.0001)
                  {:object sphere
                   :tmin smaller_root
                   :normal (vectors/scalar / r (map + (vectors/scalar * smaller_root d) (map - o c)))
                   :hit-point (map + (vectors/scalar * smaller_root d) o )}
                   (if (> larger_root 0.0001)
                    {:object sphere
                     :tmin larger_root
                     :normal (vectors/scalar / r (map + (vectors/scalar * larger_root d) (map - o c)))
                     :hit-point (map + (vectors/scalar * larger_root d) o )})))))))


(defn make-sphere
  [center radius material]
  {:pre [(and (number? radius) (pos? radius))]}
    (Sphere. center radius material))

