(defproject html5-walker "2019-10-01"
  :description "Search and replace html5."
  :url "https://github.com/magnars/html5-walker"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[ch.digitalfondue.jfiveparse/jfiveparse "0.5.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.1"]
                                  [lambdaisland/kaocha "0.0-529"]
                                  [kaocha-noyoda "2019-06-03"]]
                   :aliases {"kaocha" ["run" "-m" "kaocha.runner"]}}})
