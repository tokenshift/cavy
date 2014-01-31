(defproject cavy "0.1.2-SNAPSHOT"
  :description "Clojure HTTP client for UI automation and testing."
  :url "https://github.com/tokenshift/cavy"
  :license {:name "The MIT License (MIT)"
            :url "https://raw.github.com/tokenshift/cavy/master/LICENSE"}
  :profiles {:dev {:plugins [[lein-cloverage "1.0.2"]]}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.8"]
                 [clojurewerkz/urly "1.0.0"]
                 [com.cemerick/url "0.1.0"]
                 [enlive "1.1.5"]])
