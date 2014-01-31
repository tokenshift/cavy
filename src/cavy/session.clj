(ns cavy.session
  "The core Cavy session state."
  (:require [clojure.string :as str]
            [cavy.cookies :as cookies]
            [cavy.http :as http]
            [clojurewerkz.urly.core :as urly]
            [net.cgrand.enlive-html :as html])
  (:use cavy.util))

(defn absolute-url
  "Constructs an absolute URL given the current location and a relative path."
  [session & path]
  (if (session :location)
    (urly/resolve (session :location) (str/join "/" path))
    (str/join "/" path)))

(defn create
  "Creates a new session."
  [& options]
  (let [options (apply hash-map options)
        location (:url options)
        client (or (:client options) (http/create-client))]
    {:options options
     :client client
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

(defn redirect
  "Returns the redirect location (e.g. from a 302) for the session."
  [session]
  nil)

(defn request
  "Sends an HTTP request to the specified URL."
  [session method url & options]
  (let [options (apply hash-map options)
        response (http/request (session :client) method url options)]
    (assoc session
           :location url
           :response response
           :status (:status response)
           :page (when (is-html response) (parse-html response)))))

(defn follow-redirects
  "Follows a series of redirects until the session resolves."
  [session]
  (loop [session session]
    (let [{status :status {{location "location"} :headers} :response} session]
      (if (and location (contains? #{301 302 303 307} status))
        (recur (request session :get (absolute-url session location)))
        session))))
