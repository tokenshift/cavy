(ns cavy.http
  "Abstracts basic HTTP client functionality to enable mocking."
  (:require [clj-http.client :as web]))

(defprotocol Client
  (request [this method url]
           [this method url options]))

(defn create-client
  "Uses clj-http to make HTTP requests."
  []
  (let [cookies (clj-http.cookies/cookie-store)]
    (reify Client
      (request [this method url]
        (binding [clj-http.core/*cookie-store* cookies]
          (web/request {:method method
                        :url url})))
      (request [this method url options]
        (binding [clj-http.core/*cookie-store* cookies]
          (web/request (merge {:method method
                               :url url}
                              options)))))))
