(require '[lime.shapes :as shapes]
         '[lime.tracers]
         '[lime.light]
         '[lime.materials])
  (import '(lime.plane Plane)
          '(lime.sphere Sphere)
          '(lime.tracers Raycast)
          '(lime.light Ambient PointLight Directional Lambertian GlossySpecular)
          '(lime.materials Matte Phong))
  (defn buildworld []
    {
            :tracer (Raycast.)
            :ambient (Ambient. 1 [0.5 0.5 0.5])
            :view {
                  :vres 480
                  :hres 640
                  :size 1
                  :samples 16
                  :sampling-algorithm "jitter"
                  :default-colour [1.0 0.99215 0.81568]}
            :camera {:type "pinhole"
                     :eye [0.0 130.0 500.0]
                     :look-at [0.0 0.0 -300.0]
                     :up [0.0 1.0 0.0]
                     :distance 100
                     :zoom 4 
                     }
            :scene {
                   :objects [
                             (Sphere. 
                                    [0.0 85.0 0.0] ; center
                                    85.0 ; radius
                               (Matte. (Lambertian. 0.25 [0.65098 0.48235 0.35686])
                                       (Lambertian. 0.65 [0.65098 0.48235 0.35686])))
                             (Sphere. 
                                    [-150.0 50.0 0.0] ; center
                                    50.0 ; radius
                               (Matte. (Lambertian. 0.30 [1.0 0.866666 0.79215])
                                       (Lambertian. 0.65 [1.0 0.866666 0.79215])))
                             (Sphere. 
                                    [130.0 25.0 0.0] ; center
                                    25.0 ; radius
                               (Matte. (Lambertian. 0.30 [0.94117 0.86274 0.50980])
                                       (Lambertian. 0.65 [0.94117 0.86274 0.50980])))
                             (Plane. [0.0 1.0 0.0] [0.0 0.0 0.0]
                                     (Matte. (Lambertian. 0.30 [0.7647 0.69019 0.56862])
                                             (Lambertian. 0.30 [0.7647 0.69019 0.56862])))
                            ]
                   :lights [(PointLight. 4 [1.0 1.0 1.0] [40.0 200.0 120.0])]}})
