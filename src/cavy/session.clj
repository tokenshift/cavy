(ns cavy.session
  "The core Cavy session state."
  (:require [cavy.cookies :as cookies]
            [cavy.http :as http]))

(defn create
  "Creates a new session."
  [& options]
  (let [options (apply hash-map options)
        cookies (or (:cookies options) (cookies/mem-store))
        location (:url options)
        client (or (:client options) http/client)]
    {:options options
     :client client
     :cookies cookies
     :location location
     :response nil
     :status nil
     :page nil}))
