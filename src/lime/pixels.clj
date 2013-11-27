(ns lime.pixels)

(require '[lime.vectors :as vectors])
(require '[lime.debug :as dbg])

(defn anti-alias
  "Computes anti-aliasing on a sequence of subpixel colours.
  `world` should be a proper world structure, containing a `view` where the number of samples is expected to be found.
  Returns a unique anti-aliased colour."
  [world subpixels-colours]
  (let [final-colour (vectors/scalar / ((world :view) :samples)
                                    (reduce (fn [u v] (map + u v)) subpixels-colours))]
   final-colour)) 
