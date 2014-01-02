(ns cavy.test.unit.query
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.query :as query])
  (:use [clojure.test]))

(def page
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

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
