(ns cavy.test.functional.session
  "Fake HTTP session that serves local HTML files."
  (:require [cemerick.url :as urls]
            [cavy.session :as session]
            [cavy.http :as http]))

(defn test-client
  "Constructs a test client that will record the last request in an atom."
  [req-ref cookies]
  (reify http/Client
    (request [this method url] (http/request this method url nil))
    (request [this method url options]
      (dosync (ref-set req-ref {:method method
                                :url url
                                :options options}))
      (let [url (urls/url url)
            path (:path url)]
        (try
          (let [body (slurp (str "test/cavy/test/test-pages" path))]
            {:status 200
             :headers {"content-type" "text/html"}
             :cookies cookies
             :body body})
          (catch java.io.FileNotFoundException e
            {:status 404
             :body nil}))))))

(defn test-session
  "Create a new test session.
  If cookies are provided, the 'server' will respond with those cookies."
  [& [start-page cookies]]
  (let [req-ref (ref nil)]
    (-> (session/create :client (test-client req-ref cookies))
        (assoc :last-request req-ref)
        (session/request :get start-page))))
