(ns cavy.test.unit.session
  (:require [cavy.http :as http]
            [cavy.session :as session])
  (:use clojure.test))

(defn recorded-client
  "Constructs a test client that will return a series of predefined responses."
  [& responses]
  (let [responses (atom responses)]
    (reify http/Client
      (request [this method url] (http/request this method url nil))
      (request [this method url options]
        (let [response (first @responses)]
          (swap! responses rest)
          (or response {:status 500 :body "No more recorded responses."}))))))

(defn redirect
  "Constructs a redirect response."
  [suffix]
  {:status 302 :headers {"location" (str "/page" suffix)}})

(def final {:status 200 :body "Got here"})

(deftest follow-redirects
  (testing "Follows a single redirect"
    (let [result (session/follow-redirects
                   (-> (session/create :client (recorded-client (redirect 1) final))
                       (session/request :get "http://whatever.com")))]
      (is (= 200 (-> result :response :status)))
      (is (.contains (-> result :response :body) "Got here"))))
  (testing "Follows multiple redirects")
  (testing "Follows a maximum of 5 redirects"))
