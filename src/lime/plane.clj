(ns lime.plane)

(require '[lime.vectors :as vectors])
(require '[lime.shapes :refer [Shape]])
(require '[lime.debug :as dbg])

(defrecord Plane [normal point material]
  Shape
  (hit [plane ray]
    (let [n (vectors/normal (:normal plane))
        p (:point plane)
        d (:direction ray)
        o (:origin ray)
        t (/ (vectors/dot (map - p o) n) (vectors/dot d n))]
      (if (> t 0.001)
        {:object plane
         :tmin t
         :normal n
         :hit-point (map + o (vectors/scalar * t d))}))))
