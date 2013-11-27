(ns lime.ray
  (:require [lime.shapes :as shapes]
            [lime.sphere :as sphere]
            [lime.plane :as plane]
            ;[lime.light :as light]
            [lime.vectors :as vectors]
            [lime.debug :as dbg]))

(defn direction
  "Computes the direction of a ray for a subpixel given the camera coordinates, the look-at point and the distance between the camera and the view plane."
  [camera [x y z]]
  (let [  
        e (:eye camera)
        l (:look-at camera)
        up (:up camera)
        distance (:distance camera)
        w (vectors/normal (map - e l))
        u (vectors/normal (vectors/cross up w))
        v (vectors/cross w u)]
    (vectors/normal (vec (map - 
                              (map + (vectors/scalar * x u) (vectors/scalar * y v))
                              (vectors/scalar * distance w))))))


(defn hit
  "Computes the closest hit for a given ray and a given object.
  The function dispatches the call to hit according to the shape of the object.
  "
  [ray object]
  (.hit object ray))

(defn comp-hit-z
  "Compares two hit locations and sorts them by descending z order, i.e. from closest to farthest. If two hits happen at the same z coordinate, the first one is designated as the closest one."
  [hit1 hit2]
  (let [[_ _ z1] (hit1 :hit-location)
        [_ _ z2] (hit2 :hit-location)]
    (if (or (> z1 z2)
            (= z1 z2))
      true
      false)))


(defn trace
  "Traces a ray from a given position and returns the pixel colour according to the closest hit point.
  This function relies on the objects to compute the actual hits (see the function `hit`) and on `light/shading` to compute the colour."
  [world subpixel]
  (let [ray {:origin ((world :camera) :eye)
             :direction (direction (:camera world) subpixel)
             :parameter 0.0}
        scene (:scene world)
        view (:view world)
        objects (:objects scene)
        hits (sort-by :tmin < (remove nil? (map #(hit ray %) objects)))]
        [ray (first hits)]))
