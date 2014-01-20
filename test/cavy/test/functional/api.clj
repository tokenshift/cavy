(ns cavy.test.functional.api
  (:require [net.cgrand.enlive-html :as enlive]
            cavy)
  (:use clojure.test
        cavy.test.functional.session))

(defn check-session
  "Runs a predicate against the session."
  [session pred]
  (is (pred session))
  session)

(defmacro with-session
  "Helper to thread a session through an arbitrary form."
  [session name & forms]
  `((fn [~name] ~@forms ~name) ~session))

(defmacro does-nothing
  "Verifies that the specified action(s) does not provoke navigation."
  [session & forms]
  `((fn [s#]
      (-> s#
          (assoc :location ::did-nothing)
          ~@forms
          (check-session #(= ::did-nothing (:location %1)))))
      ~session))

(defn went-to
  "Verifies that the session has navigated to the specified URL."
  [session url]
  (is (= url (:location session)))
  session)

(deftest click
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
        (went-to "http://example2.com/link.html")))
  (testing "link not found"
    (-> (test-session "http://example.com/link1.html")
        (does-nothing (cavy/click "whatever")))))

(deftest fill-in
  (testing "login fields"
    (let [result (-> (test-session "http://example.com/login.html")
                     (cavy/fill-in "Username" "test-user")
                     (cavy/fill-in "Password" "test-password"))
          page (result :page)
          username (first (enlive/select page [:#username]))
          password (first (enlive/select page [:#password]))]
      (is (= "test-user" (get-in username [:attrs :value])))
      (is (= "test-password" (get-in password [:attrs :value])))))
  (testing "field not found"
    (-> (test-session "http://example.com/login.html")
        (does-nothing (cavy/fill-in "Whatever" "testing")))))

(deftest press
  (testing "submitting login form"
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
  (testing "submit button with no value"
    (let [result (-> (test-session "http://example.com/form.html")
                     (cavy/fill-in "Field 1" "testing")
                     (cavy/press "Submit"))
          request @(result :last-request)]
      (is (= :post (request :method)))
      (is (= "http://example.com/test-submit" (request :url)))
      (is (= ["testing"] (-> request :options :form-params :field1)))))
  (testing "button not found"
    (-> (test-session "http://example.com/form.html")
        (does-nothing (cavy/press "whatever")))))
