(ns cavy.session
  "The core Cavy session state."
  (:require [cavy.cookies :as cookies]
            [cavy.http :as http]
            [net.cgrand.enlive-html :as html]))

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

(defn- is-html
  "Checks whether the HTTP response body is HTML."
  [response]
  (.contains ((response :headers) "content-type") "text/html"))

(defn- parse-html
  "Parses the HTML body of an HTTP response."
  [response]
  (html/html-snippet (response :body)))

(defn request
  "Sends an HTTP request to the specified URL."
  [session method url & options]
  (let [response (http/request (session :client)
                method
                url
                (apply hash-map options))]
    (assoc session
           :location url
           :response response
           :status (response :status)
           :page (when (is-html response) (parse-html response)))))
