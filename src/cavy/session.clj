(ns cavy.session
  "The core Cavy session state."
  (:require [clj-http.client :as http]))

(defn create
  "Creates a new session."
  [& options]
  (let [options (apply hash-map options)
        location (:url options)]
    {:options options
     :cookies nil
     :location location
     :page nil}))
