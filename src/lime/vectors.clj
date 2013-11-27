(ns lime.vectors)

(defn dot
  [u v]
  (apply + (map * u v)))

(defn cross
  [[x1 y1 z1] [x2 y2 z2]]
  [(- (* y1 z2) (* z1 y2))
   (- (* z1 x2) (* x1 z2))
   (- (* x1 y2) (* y1 x2))])

(defn magnitude
  [[x y z]]
  (Math/sqrt (+ (* x x) (* y y) (* z z))))

(defn scalar
  [f s v]
  (vec (map #(f % s) v)))

(defn normal
  [v]
  (scalar / (magnitude v) v))
