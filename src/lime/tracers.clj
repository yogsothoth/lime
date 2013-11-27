;; ## Tracers
;; Tracing and shading can be done is a number of ways, for various results. `lime` offers this layer for defining such particular sequence as a record definition of the Tracer protocol.
;; As of today, only the classical ray casting is implemented.
 
(ns lime.tracers)

(require '[lime.ray :as ray])
(require '[lime.debug :as dbg])

(defprotocol Tracer
    "Protocol for all kinds of tracers."
  (trace [this world depth subpixel] "Traces a ray and returns the colour at the location."))

(defrecord Raycast []
  Tracer
    (trace [this world depth subpixel]
           (let [scene (:scene world)
                 view (:view world)
                 [ray closest-hit] (ray/trace world subpixel)]
                (if-not (nil? closest-hit) 
                  (.shade (:material (:object closest-hit)) ray closest-hit world)
                  (:default-colour view)))))
