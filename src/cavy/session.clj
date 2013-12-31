(ns cavy.session
  "The core Cavy session state."
  (:require [clj-http.client :as http]
            [cavy.cookies :as cookies]))

(defn create
  "Creates a new session."
  [& options]
  (let [options (apply hash-map options)
        cookies (or (:cookies options) (cookies/mem-store))
        location (:url options)]
    {:options options
     :cookies cookies
     :location location
     :page nil}))
