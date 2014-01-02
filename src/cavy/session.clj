(ns cavy.session
  "The core Cavy session state."
  (:require [clojure.string :as str]
            [cavy.cookies :as cookies]
            [cavy.http :as http]
            [clojurewerkz.urly.core :as urly]
            [net.cgrand.enlive-html :as html])
  (:use cavy.util))

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
  (in-str? (get-in response [:headers "content-type"]) "text/html"))

(defn- parse-html
  "Parses the HTML body of an HTTP response."
  [response]
  (html/html-snippet (response :body)))

(defn request
  "Sends an HTTP request to the specified URL."
  [session method url & options]
  (let [response (http/request (session :client)
                               method url
                               (apply hash-map options))]
    (assoc session
           :location url
           :response response
           :status (:status response)
           :page (when (is-html response) (parse-html response)))))

(defn absolute-url
  "Constructs an absolute URL given the current location and a relative path."
  [session & path]
  (urly/resolve (session :location) (str/join "/" path)))
