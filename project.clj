(defproject cavy "0.1.2-SNAPSHOT"
  :description "Clojure HTTP client for UI automation and testing."
  :url "https://github.com/tokenshift/cavy"
  :license {:name "The MIT License (MIT)"
            :url "https://raw.github.com/tokenshift/cavy/master/LICENSE"}
  :profiles {:dev {:plugins [[lein-cloverage "1.0.2"]]}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-http "0.7.8"]
                 [clj-http-fake "1.0.1"]
                 [com.cemerick/url "0.1.1"]
                 [enlive "1.1.5"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.3.2"]])
