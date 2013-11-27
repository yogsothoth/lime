(defproject lime "0.1.0-SNAPSHOT"
  :description "a ray tracer that's easy and fun to hack on"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License"
            :url "http://www.gnu.org/licenses/gpl-2.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                  [org.clojure/tools.cli "0.2.4"]]
  :plugins [[lein-marginalia "0.7.1"]]
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :aot :all
  :main lime.core)
