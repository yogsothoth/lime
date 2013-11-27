(ns lime.shapes)


;; ## Protocols
;; ### Shapes
;; The Shape protocol governs all calculations determined by the shape assumed by the objects in a scene. Specifically, how hit locations are calculated depends on the shapes of the objects, so all Shapes records must implement a function called `hit`.
(defprotocol Shape
  "Protocol for all shapes."
  (hit [this ray] "Computes the hits between the shape and a given ray"))
