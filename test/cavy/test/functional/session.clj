(ns cavy.test.functional.session
  "Fake HTTP session that serves local HTML files."
  (:require [cavy.session :as session]
            [cavy.http :as http]))

(def client
  (reify http/Client
    (request [this method url] (http/request this method url nil))
    (request [this method url options]
      ; TODO Respond with local file.
      {})))

(defn test-session
  "Create a new test session."
  [& [start-page]]
  (-> (session/create :client client)
      (session/request :get start-page)))
