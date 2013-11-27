(ns lime.debug)

(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))
