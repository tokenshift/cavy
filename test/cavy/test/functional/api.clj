(ns cavy.test.functional.api
  (:require [net.cgrand.enlive-html :as enlive]
            cavy)
  (:use clojure.test
        cavy.test.functional.session))

(defn went-to
  "Verifies that the session has navigated to the specified URL."
  [session url]
  (is (= url (:location session))))

(deftest click-link
  (testing "by text"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "relative")
        (went-to "http://example.com/link-relative.html")))
  (testing "by URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "link-relative.html")
        (went-to "http://example.com/link-relative.html")))
  (testing "by selector"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click [:ul :li [:a :.test-link]])
        (went-to "http://example.com/link-with-class.html")))
  (testing "with an absolute URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "absolute")
        (went-to "http://example.com/link-absolute.html")))
  (testing "with a protocol-relative URL"
    (-> (test-session "https://example.com/link1.html")
        (cavy/click "protocol-relative")
        (went-to "https://example.com/link.html")))
  (testing "with a fully specified URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "external")
        (went-to "http://example2.com/link.html"))))

(deftest fill-in
  (let [result (-> (test-session "http://example.com/login.html")
                   (cavy/fill-in "Username" "test-user")
                   (cavy/fill-in "Password" "test-password"))
        page (result :page)
        username (first (enlive/select page [:#username]))
        password (first (enlive/select page [:#password]))]
    (is (= "test-user" (get-in username [:attrs :value])))
    (is (= "test-password" (get-in password [:attrs :value])))))

(deftest press
  (let [result (-> (test-session "http://example.com/login.html")
                   (cavy/fill-in "Username" "test-username")
                   (cavy/fill-in "Password" "test-password")
                   (cavy/fill-in "Description" "Lorem ipsum...")
                   (cavy/press "Login"))
        request @(result :last-request)]
    (is (= :post (request :method)))
    (is (= "http://example.com/login" (request :url)))
    (is (not (nil? (get-in request [:options :form-params]))))
    (is (= ["test-username"] (get-in request [:options :form-params :username])))
    (is (= ["test-password"] (get-in request [:options :form-params :password])))))

(defn check-req-cookie
  "Verifies the value of a cookie sent in the last request."
  [session key val]
  (let [cookies (get-in @(session :last-request) [:options :cookies])]
    (is (= val (get-in cookies [key]))))
  session)

(deftest cookies
  (-> (test-session "http://example.com/link1.html"
                    {"cookie1" "Foobar" "cookie2" "Fizzbuzz"})
      (check-req-cookie "cookie1" nil)
      (check-req-cookie "cookie2" nil)
      (cavy/visit "http://example.com/link2.html")
      (check-req-cookie "cookie1" "Foobar")
      (check-req-cookie "cookie2" "Fizzbuzz")))
