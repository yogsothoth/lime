;; ## A ray tracer that's easy and fun to hack on
;;
;; lime aims at being a complete ray tracer, offering all the features a modern ray tracer does. However, lime will probably never be as optimised (understand quick) as, say, Persistence of Vision, and professional-grade ones even less so. Apart from my own humble limits as a programmer, this is mostly due to the fact that I intend to have fun at both using *and* writing lime. 
;; lime is written in clojure exactly for this reason: I enjoy Lisp and I enjoy learning about the language at the same time I'm learning about ray tracing.
;; lime is extensively documented so poking at it is easy and hacking the codebase is enjoyable.
;;
;; ## About the name
;;
;; lime was first called indigo, since to me the name brings back old-time memories, when I use to fiddle with my SGI workstations until late in the night. Well, it would seem I'm not exactly the only one in this case, since both Indigo and Octane have been used to name ray tracers ! So lime it is now. I like the colour, I like the taste and it describes the development effort pretty well: it might be difficult sometimes, but you just can't stop trying to add jut one more feature...
;;
;; ## General concepts
;;
;; ### Geometrical data
;; Vectors and points are stored in the same basic data structure: vectors containing an xyz triplet, expressed as floating point numbers, like so:
;;
;;        [-1.0987 3.9876 0.0]
;; Avoiding anything more specific means the code remains as generic as possible and can rely on very standard functions and idioms.
;;
;; ### Colours
;; Colours are expressed in RGB format. A colour vector contains three values, one for each colour component, expressed as a floating point number between 0.0 and 1.0.
;;
;; ### The World structure
;; The world structure is a hashmap containing all the elements necessary to ray trace something. This includes the viewplane and the scene itself (see below for the details), as well as the type of ray tracing to be done, the camera to use and the default ambient colour.
;;
;;        {:tracer (Raycast.)
;;         :ambient (Ambient. 1 [0.5 0.5 0.5])
;;         :camera {....}
;;         :view {....}
;;         :scene {...}}
;; The world is used by most top-level functions, like render and trace, since they need some informations found in the scene or the view structures, and is therefore their first parameter.
;;
;; ### The tracer
;; lime can work with different tracing algorithms. These determine how shading is applied (not how much, this is left to the materials). Today, only one algorithm is proposed: Ray Casting, implemented by the record `Raycast`.
;;
;; ### The ambient colour
;; The ambient colour specified in the world structure concerns the basic global lighting colour to be found in the scene. If no light is specified later on, this colour will be the only contribution. Specifying the default ambient colour is done by creating an `Ambient` record with an intensity and a colour, and putting it at the top-level of the `world` hierarchy, under the key `ambient`.
;;
;; ### The camera
;; lime provides means for working with different kinds of camera. The camera is responsible for rendering the perspective of a scene, and provides an interface for zooming in and out on a scene. Cameras are defined as free-hand camera, meaning they can be placed anywhere in the scene and can look at any point in the scene. A cemera is specified as follows:
;; 
;;            {:type "pinhole"
;;             :eye [0.0 130.0 500.0]
;;             :look-at [0.0 0.0 -300.0]
;;             :up [0.0 1.0 0.0]
;;             :distance 100
;;             :zoom 4}
;;
;; The `type` must be a string giving the name of the camera implementation.Today, only one kind of camera is supported, the `pinhole` camera. The `eye` indicates the position of the camera, given as an xyz triplet, and the look-at is the point towards which the camera is oriented, also specified as the usual xyz coordinates triplet. The `up` field is useful for rotating the camera around the Z-axis, and is specified as a unit-vector. In the example above, the up is along the positive Y-axis. The `distance` field allows for positioning the camera relatively to the viewplane. This is different from the absolute position of the camera, as given by its `eye` field. No matter where the camera is positioned using `eye`, lime must know how far the viewplane is from the camera. This has an influence on how much of a scene will finally be rendered. At last, the `zoom` field, specified typically as an integer, provides a simple interface for zooming in and out. The same can be achieved by plaing with the pixel size, but specifying a zoom value seems more intuitive.
;;
;; ### The view structure
;; The view structure can be found in the world structure and describes the viewplane object. The viewplane has a very strong influence on the rendering: the resolution, pixels, sampling technique and number of samples, eye and camera location and direction, the default colour for orphan pixels... The view is implemented as a simple hashmap:
;;
;;            {:vres 768
;;             :hres 1024
;;             :size 0.5
;;             :samples 16
;;             :sampling-algorithm "jitter"
;;             :default-colour [0.0 0.0 0.0]}
;;
;;Note that the fields `:vres` `:hres` must be specified on the command line and that `:size`, `:samples` and `:sampling-algorithm` can either be provided on the command line or in the structure.
;;
;; ### The scene structure
;; The scene structure contains the definition of all the objects that can be found in the scene, as well as a description of all the lights. The scene structure is implemented as a hashmap:
;;
;;
;;            {:objects [...
;;                       ...
;;                        ]
;;             :lights [...
;;                      ...
;;                      ]}
;;
;; ### Objects 
;;  The objects list hold multiple instances of `Shape` protocol records, each pertaining to one object in the scene: a sphere, a plane, a triangle surface. The exact nature of this information varies depending on the shape of the object. As of today, only the sphere is properly implemented. An object is implemented as a record for the `Shape` protocol. Each object also holds information on the material of the object.
;;
;; ### Materials 
;; The material slot in `Shape` records holds the details on how a given surface reflects the light it receives. Through *Bidirectional Reflectance Distribution Functions*, three kinds of lights are handled: *ambient*, *diffuse* and *specular*. Ambient and Diffuse BRDFs are typically instanciated as Lambertian and offer two parameters, `ls` and `colour`, handling respectively the intensity and the colour of the light contribution. Specular highlights are handled with the `GlossySpecular` BRDF, which follows mostly the original Phong equation, where the shininess is elevated at a given exponent. 
;;
;; ### Lights 
;; The light records implement the Light protocol and hold the definition of lights to be used in the scene. As for the shapes, the exact information in the records depends on the type of light. As of today, only ambient and point light types are available. 
;;
;; ### Complete world example
;; A complete world example can be found in the directory `examples`.
;;
;; ## Command-line interface
;; lime provides a user interface on the command-line. The expected parameters and options are as follows:
;; 
;;   - Mandatory options
;;      - `-f` or `--file`: the path to scene file (see documentation above)
;;      - `-o` or `--output`: the path to the output file (picture file)
;;      - `-h` or `--height`: the height of the rendered picture
;;      - `-w` or `--width`: the width of the rendered picture
;;   - Optional, errr, options:
;;      - None at the moment.
;;
;; Whenever a mandatory options is not provided on the command line, lime prints an error message and exits.
;;
;; ## Scene files
;; lime expects the user to provide lime with a scene file, written in clojure, and specified on the command line behind the `-f` or `--file` options. The scene file must be written in clojure, must contain a single function that returns a world structure. This file will be read and evaluated by lime.
;;

(ns lime.core
  (:require [lime.ray :as ray]
            [lime.shapes :as shapes]
            [lime.tracers]
            [lime.light]
            [lime.materials]
           [lime.pixels :as pixels]
           [lime.sampling :as sampling]
           [lime.debug :as dbg]
            [clojure.tools.cli :refer [cli]])
  ;; We rely on good old Swing for display
  (:import (lime.plane Plane)
           (lime.sphere Sphere)
           (lime.tracers Raycast)
           (lime.light Ambient PointLight Directional Lambertian GlossySpecular)
           (lime.materials Matte Phong)
           (java.awt Color Dimension)
           (java.awt.image BufferedImage)
           (javax.swing.JFrame)
           (javax.imageio.ImageIO)
           (java.io File))
  (:gen-class
    :methods [#^{:static true} [go [java.lang.String] java.lang.Boolean]]))

;; ## Rendering
;; Rendering is done pixel by pixel, with tracing happening on a subpixel basis and anti-aliasing being applied to a set of subpixels, to give the final pixel colour.
;;

(defn trace
  "Traces a ray for each subpixel in the sequence and returns a vector containing the colour for each subpixel.
  `world` must be a proper world structure.
  `subpixels` must be a sequence of coordinates triplets, `{:x :y :z}`, as returned by `sampling/sample`."
  [world tracer subpixels]
  (map #(.trace tracer world nil %) subpixels))

(defn render
  "Initiates the rendering of a pixel.
  A pixel is first broken into subpixels (sampling stage), then each subpixel is calculated (intersection and shading stage). An anti-aliasing function is then applied to each pixel set of subpixels, to calculate the final colour of the pixel. The value returned is a structure of the form 
  
        {:row
         :column
         :colour}
  where row and column are integers corresponding to the current row and column in the view, starting at 0, and colour is a vector containing the three values for R, G and B.
  The sampling algorithm is expected to be one of the following strings: *regular*, *jitter* or *random*.
  "
  [world pixel]
  (let [r (pixel :row)
        c (pixel :column)
        view (world :view)
        algorithm (view :sampling-algorithm)
        camera (world :camera)
        tracer (world :tracer)
        pixel-colour (pixels/anti-alias world
                       (trace world tracer
                         (sampling/sample algorithm camera view c r)))]
    {:row r
     :column c
     :colour pixel-colour}))

;; ## Image processing
;;

(defn display-image
  "Displays the rendered image as a `BufferedImage` object inside a `JFrame`.
  The width and height give the resolution of the image (typically taken from the `view` structure) and the rendered-image is a sequence holding colour vectors for all pixels."
  [width height rendered-image]
  (let [image (java.awt.image.BufferedImage.
               width height java.awt.image.BufferedImage/TYPE_INT_RGB)
        graphics (.createGraphics image)]
    (println "Rendering image...")
    (doseq [pixel rendered-image]
      (let [[r g b] (pixel :colour)
            x (pixel :column)
            y (- height (pixel :row))]
        (.setColor graphics (java.awt.Color.
                              (float (min r 1.0))
                              (float (min g 1.0))
                              (float (min b 1.0))))
        (.drawLine graphics x y x y)))
    (println "Drawing image on screen...")
    (doto (javax.swing.JFrame.)
      (.add (proxy [javax.swing.JPanel] []
              (paint [g] (.drawImage g image 0 0 this))))
      (.setSize (java.awt.Dimension. width height))
      (.show))))

(defn prepare-image
  "Writes the rendered image as a PNG file.
  The width and height give the resolution of the image (typically taken from the `
view` structure) and the rendered-image is a sequence holding colour vectors for al
l pixels. The last argument, output, gives the path to the output file."
  [width height rendered-image output]
  (let [image (java.awt.image.BufferedImage.
               width height java.awt.image.BufferedImage/TYPE_INT_RGB)
        graphics (.createGraphics image)]
    (println "Rendering image...")
    (doseq [pixel rendered-image]
      (let [[r g b] (pixel :colour)
            x (pixel :column)
            y (- height (pixel :row))]
        (.setColor graphics (java.awt.Color.
                              (float (min r 1.0))
                              (float (min g 1.0))
                              (float (min b 1.0))))
        (.drawLine graphics x y x y)))
    (println "Writing image to file")
    (javax.imageio.ImageIO/write image "png" (File. output))))


;; ## Command-line interface
;;

(defn parse-cli
  "This function is responsible for parsing the command line, looking for all arguments and options, and terminating the program with an explicit error message in case something mandatory is missing."
  [args]
  (let [[options other-args banner]
        (try (cli args
             ["-f" "--file" "Scene file"]
             ["-o" "--output" "Output file"]
             ["-z" "--size" "Pixel size" :default 0.5 :parse-fn #(Float. %)]
             ["-n" "--samples" "Number of samples" :default 16 :parse-fn #(Integer. %)]
             ["-s" "--sampling" "Sampling algorithm" :default "jitter"]
             ["-w" "--width" "Width of the rendered image" :parse-fn #(Integer. %)]
             ["-h" "--height" "Height of the rendered image" :parse-fn #(Integer. %)])
             (catch java.lang.Exception e
               (do
               (println "Error while parsing the command line:" (.getMessage e))
               (System/exit 0))))]
    ;We bail out in case mandatory options are missing.
    (when-not (and
              (:file options)
              (:width options)
              (:height options))
          (println banner)
          (System/exit 0))
    ; All required options are here, return the data
    [options other-args]))

(defn check-cli
  [[options other-args]]
  (let [scene (.getAbsoluteFile (File. (:file options)))
        output (.getAbsoluteFile (File. (:output options)))]
  (when-not (and
             (.exists scene)
             (.isFile scene)
             (.canRead scene))
      (println "Cannot read scene file " (.getAbsolutePath scene) ".")
      (System/exit 0))
    (when-not 
             (.canWrite (.getParentFile output))
        (println "Cannot write in the directory for output file " (.getAbsolutePath output) ".")
        (System/exit 0))
    [options other-args]))

(defn merge-view-in
  "Merges the view as defined in the buildworld function in the scene file with the relevant options found on the command line."
  [world options]
  ;(assoc world :view
  (update-in world [:view]
         ;(merge (:view world)
         merge 
                {:vres (:height options)
                 :hres (:width options)
                 :size (:size options)
                 :samples (:samples options)
                 :sampling-algorithm (:sampling options)}))

(defn camera-valid?
  [camera]
  (not= (:eye camera) (:look-at camera)))

(defn validate
  [world]
  (if-not (camera-valid? (:camera world))
    (do
      (println "Error: the camera is not valid. Exiting.")
      (System/exit 0))
    world))

; forward declaration of the function in we expect to find in the scene file.
(declare buildworld)


(defn -main
  "In the main function, we:
          
   1. Call parse-cli to extract the arguments. If no output file is given, the scene is written to a file called lime-scene.png in the current directory.
   2. Parse and evaluate the scene file, which is expected to contain a worldbuilding function. The world must be a structure as described above in the documentation.
   3. Compute a vector holding all our pixels, just so we can `map` over them.
   4. Call `render` for each pixel in the sequence. Typically done with `map`. Note that using `pmap` here instead of just map allows us to do the rendering in parallel, using the standard 2+n threads, where n is equal to the number of cores n the CPU running our JVM.
   5. Call `prepare-image` when all pixels are available, producing the output picture file, in PNG format.
  "
  
  [& args]
  (time
    (let [[options other-args] (check-cli (parse-cli args))
          buildworld (load-string (slurp (:file options)))
          world (validate (merge-view-in (buildworld) options))
          output (if (:output options) (:output options) "lime-scene.png")
          pixels (for [y (range 0 (get-in world [:view :vres]))
                       x (range 0 (get-in world [:view :hres]))]
                       {:column x :row y})]
      (prepare-image (get-in world [:view :hres]) (get-in world [:view :vres])
                     (pmap #(render world %) pixels) output)))) 


(defn -go
  "This is the main Java interface for Interop. Here we expect a single argument: the path to the scene file. All it does today is call the -main function, passing it the argument untouched."
  [file]
  (-main file))

