(ns lime.core-test
  (:require [clojure.test :refer :all]
            [lime.core :refer :all]))


; CLI

(deftest parse-cli-correct-long
  (is (= [{:file "/path/to/scene.clj"
           :output "/path/to/output.png"
           :width 1024
           :height 768
           :size 0.5
           :samples 16
           :sampling "jitter"}
          []]
         (parse-cli ["--file" "/path/to/scene.clj"
                    "--output" "/path/to/output.png"
                     "--width" "1024"
                     "--height" "768"])))
  "Parse CLI positive test, long option names")

(deftest parse-cli-correct-short
  (is (= [{:file "/path/to/scene.clj"
           :output "/path/to/output.png"
           :width 1024
           :height 768
           :size 0.5
           :samples 16
           :sampling "jitter"}
          []]
         (parse-cli ["-f" "/path/to/scene.clj"
                    "-o" "/path/to/output.png"
                     "-w" "1024"
                     "-h" "768"])))
  "Parse CLI positive test, short option names")

(deftest parse-cli-override
  (is (= [{:file "/path/to/scene.clj"
           :output "/path/to/output.png"
           :width 1024
           :height 768
           :size 1.0
           :samples 64
           :sampling "simple"}
          []]
         (parse-cli ["-f" "/path/to/scene.clj"
                     "-o" "/path/to/output.png"
                     "-w" "1024"
                     "-h" "768"
                     "-z" "1"
                     "-s" "simple"
                     "-n" "64"])))
  "Parse CLI, overriding defaults for size, samples and sampling")


(deftest view-invalid-eye-look-at
  (is (false?
              (camera-valid? {:type "pinhole"
                              :eye [10.0 20.0 30.0]
                              :look-at [10.0 20.0 30.0]
                              :up [0.0 1.0 0.0]
                              :distance 100
                              :zoom 4})))
      "eye position and look-at point must be different for the camera")
