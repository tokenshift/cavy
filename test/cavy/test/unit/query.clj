(ns cavy.test.unit.query
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.query :as query])
  (:use [clojure.test]))

(def page
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

(def test-form
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/login.html")))

(def test-form-filled
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/login-filled.html")))

(deftest find-button
  (testing "by text"
    (let [button (query/find-button test-form "Login")]
      (is (not (nil? button)))
      (is (= "Login" (get-in button [:attrs :value])))))
  (testing "by selector"
    (let [button (query/find-button test-form [(enlive/attr= :type "submit")])]
      (is (not (nil? button)))
      (is (= "Login" (get-in button [:attrs :value]))))))

(deftest find-form-with-button
  (let [[form button] (query/find-form-with-button test-form "Login")]
    (is (not (nil? form)))
    (is (not (nil? button)))
    (is (= "login-form" (get-in form [:attrs :id])))
    (is (= "login-button" (get-in button [:attrs :id])))))

(deftest find-label
  (testing "without colon"
    (let [label (query/find-label page "Field 1")]
      (is (= "field1" (get-in label [:attrs :for])))))
  (testing "with colon"
    (let [label (query/find-label page "Field 2")]
      (is (= "label2" (get-in label [:attrs :id]))))))

(deftest find-by-label
  (testing "label for"
    (let [field (query/find-by-label page "Field 1")]
      (is (= "field1" (get-in field [:attrs :id])))))
  (testing "labelled by"
    (let [field (query/find-by-label page "Field 2")]
      (is (= "label2" (get-in field [:attrs :aria-labelledby])))))
  (testing "label"
    (let [field (query/find-by-label page "Field 3")]
      (is (= "Field 3" (get-in field [:attrs :aria-label]))))))

(deftest find-link
  (testing "by text"
    (let [link (query/find-link page "Link 4")]
      (is (= "test4.html" (get-in link [:attrs :href])))))
  (testing "by href"
    (let [link (query/find-link page "test2.html")]
      (is (= "test2.html" (get-in link [:attrs :href])))))
  (testing "by enlive selector")
    (let [link (query/find-link page [:ul :li :a.foo])]
      (is (= "test3.html" (get-in link [:attrs :href])))))

(deftest get-form-fields
  (let [form (first (enlive/select test-form-filled [:form#login-form]))
        fields (query/get-form-fields form)]
    (is (not (nil? fields)))
    (is (= ["test-username"] (:username fields)))
    (is (= ["test-password"] (:password fields)))
    (is (= ["Lorem ipsum dolor sit amet consectetur..."] (:description fields)))))
