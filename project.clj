(defproject cavy "0.0.1-SNAPSHOT"
  :description "Clojure HTTP client for UI automation and testing."
  :url "https://github.com/tokenshift/cavy"
  :license {:name "The MIT License (MIT)"
            :url "https://raw.github.com/tokenshift/cavy/master/LICENSE"}
  :profiles {:dev {:dependencies [[midje "1.5.1"]]
                   :plugins [[lein-midje "3.0.0"]]}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.8"]
                 [clojurewerkz/urly "1.0.0"]
                 [com.cemerick/url "0.1.0"]
                 [enlive "1.1.5"]])
