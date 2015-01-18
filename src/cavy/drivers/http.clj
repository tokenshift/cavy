(ns cavy.drivers.http
  "An HTTP-based driver (wrapping clj-http) to hit external applications and
  simulate a persistent client session without running an actual browser."
  (:require [cemerick.url :as u]
            [clj-http.client :as http]
            [net.cgrand.enlive-html :as enlive]
            [cavy]))

(defrecord Session [cookies]
  cavy/Session
  (visit [this url]
    (let [response (http/get url)
          page (-> response :body enlive/html-snippet first)]
      (-> this
          (assoc ::response response)
          (assoc ::url url)
          (assoc ::page page))))

  (body [this] (-> this ::response :body))
  (headers [this] (-> this ::response :headers))
  (path [this] (-> this ::url u/url :path))
  (query [this] (-> this ::url u/url :query))
  (status [this] (-> this ::response :status))
  (text [this] (-> this ::page enlive/text))
  (url [this] (-> this ::url)))

(defn create-session
  "Create a new HTTP driver session."
  [options]
  (let [cookies (clj-http.cookies/cookie-store)]
    (->Session cookies)))
