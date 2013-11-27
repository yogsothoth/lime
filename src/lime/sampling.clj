(ns lime.sampling
  (:require [lime.vectors :as vectors]
            [lime.debug :as dbg]))

(defn sample 
  "Computes sampling for a pixel. 
  Returns a vector of subpixels, whose exact location depends on the algorithm used:

  - *regular*: The pixel is divided in an n*n grid. Subpixels are at the center of each cell. 
  - *random*:  Subpixels scattered inside the pixel.
  - *jitter*:  The pixel is divided in an n*n grid. Subpixels are in a random location inside each cell."
  [algorithm camera view column row]
  (let [n (Math/sqrt (:samples view))
        subpix-offsets (cond 
                 (= "regular" algorithm)
                 (for [p (range 0 n)
                       q (range 0 n)]
                   {:p (/ (+ p 0.5) n) :q (/ (+ q 0.5) n)})
                 (= "random" algorithm)
                 (for [p (range 0 n)
                       q (range 0 n)]
                   {:p (rand 1) :q (rand 1)})
                 (= "jitter" algorithm)
                 (for [p (range 0 n)
                       q (range 0 n)]
                   {:p (/ (+ p (rand 1)) n) :q (/ (+ q (rand 1)) n)}))
        c column
        r row]
        (vec
          (map (fn
                 [pixoffset]
                 (let [x (* (+ (- c (* 0.5 (:hres view))) (:q pixoffset)) (/ (:size view) (:zoom camera)))
                       y (* (+ (- r (* 0.5 (:vres view))) (:p pixoffset)) (/ (:size view) (:zoom camera)))]
                   [x y 100])) subpix-offsets))))
