(ns cavy.http
  "Abstracts basic HTTP client functionality to enable mocking."
  (:require [clj-http.client :as web]))

(defprotocol Client
  (request [this method url]
           [this method url options]))

(def client
  "Uses clj-http to make HTTP requests."
  (reify Client
    (request [this method url]
      (web/request {:method method
                    :url url}))
    (request [this method url options]
      (web/request (merge {:method method
                           :url url}
                          options)))))

(defn test-client
  "Records requests without sending them anywhere."
  [& [response]]
  (let [req (atom nil)]
    (reify Client
      (request [this method url]
        (request this method url nil))
      (request [this method url options]
        (dosync
          (swap! req {:method method
                      :url url
                      :options options}))
        response))))
