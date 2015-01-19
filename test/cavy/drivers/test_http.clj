(ns cavy.drivers.test-http
  "Tests for the HTTP (clj-http) Cavy driver."
  (:require [clojure.test :refer :all]
            [cavy :refer :all]
            [cavy.drivers])
  (:use clj-http.fake))

(deftest test-visit
  (testing "makes a GET request"
    (let [req (atom nil)]
      (with-fake-routes
        {#"http://example.com/.*"
         (fn [r]
           (reset! req r)
           {:status 200
            :body "Hello"})}
        (let [result (-> (session cavy.drivers/http)
                         (visit "http://example.com/test-path"))]
          (is (not (nil? @req)))
          (is (= :get (:request-method @req)))
          (is (= :http (:scheme @req)))
          (is (= "example.com" (:server-name @req)))
          (is (= "/test-path" (:uri @req)))))))
  (testing "processing response fields"
    (with-fake-routes
      { #"http://example.com/.*"
       (fn [r]
         {:status 201
          :headers {:foo "Bar"}
          :body "<div>Hello!</div>"})}
      (let [result (-> (session cavy.drivers/http)
                       (visit "http://example.com/some/fake/path?hello=goodbye"))]
        (is (= "<div>Hello!</div>" (body result)))
        (is (= {:foo "Bar"} (headers result)))
        (is (= "/some/fake/path" (path result)))
        (is (= {"hello" "goodbye"} (query result)))
        (is (= 201 (status result)))
        (is (= "Hello!" (text result)))
        (is (= "http://example.com/some/fake/path?hello=goodbye" (url result))))))
  (testing "URL after redirects"
    (with-fake-routes
      {"http://example.com/page1"
       (fn [r] {:status 302
                :headers { "Location" "http://example.com/page2" }
                :body ""})
       "http://example.com/page2"
       (fn [r] {:status 302
                :headers { "Location" "http://example.com/page3" }
                :body ""})
       "http://example.com/page3"
       (fn [r] {:status 200
                :body "Done!"})}
      (let [result (-> (session cavy.drivers/http)
                       (visit "http://example.com/page1"))]
        (is (= 200 (status result)))
        (is (= "http://example.com/page3" (url result)))
        (is (= "Done!" (body result)))))))
