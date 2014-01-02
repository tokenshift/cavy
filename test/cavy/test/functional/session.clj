(ns cavy.test.functional.session
  "Fake HTTP session that serves local HTML files."
  (:require [cemerick.url :as urls]
            [cavy.session :as session]
            [cavy.http :as http]))

(def client
  (reify http/Client
    (request [this method url] (http/request this method url nil))
    (request [this method url options]
      (let [url (urls/url url)
            path (:path url)]
        (try
          (let [body (slurp (str "test/cavy/test/test-pages" path))]
            {:status 200
             :headers {"content-type" "text/html"}
             :body body})
          (catch java.io.FileNotFoundException e
            {:status 404
             :body nil}))))))

(defn test-session
  "Create a new test session."
  [& [start-page]]
  (-> (session/create :client client)
      (session/request :get start-page)))
