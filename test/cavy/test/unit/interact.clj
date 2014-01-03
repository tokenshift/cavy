(ns cavy.test.unit.interact
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.interact :as interact])
  (:use clojure.test))

(def page
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

(deftest set-checked
  (testing "checked = true"
    (let [result (interact/set-checked page "Field 5" true)
          field (first (enlive/select result [:#field5]))]
      (is (= "checked" (get-in field [:attrs :checked])))))
  (testing "checked = false"
    (let [result (interact/set-checked page "Field 5" false)
          field (first (enlive/select result [:#field5]))]
      (is (nil? (get-in field [:attrs :checked])))
      (is (not (contains? (field :attrs) :checked)))))
  (testing "checked = toggle"
    (let [result1 (interact/set-checked page "Field 5" :toggle)
          result2 (interact/set-checked result1 "Field 5" :toggle)
          field1 (first (enlive/select result1 [:#field5]))
          field2 (first (enlive/select result2 [:#field5]))]
      (is (= "checked" (get-in field1 [:attrs :checked])))
      (is (nil? (get-in field2 [:attrs :checked]))))))

(deftest set-field-value
  (testing "by label"
    (let [result (interact/set-field-value page "Field 1" "Testing")
          field (first (enlive/select result [:#field1]))
          value (get-in field [:attrs :value])]
      (is (= "Testing" value))))
  (testing "by selector"
    (let [result (interact/set-field-value page [:#field1] "Testing 2")
          field (first (enlive/select result [:#field1]))
          value (get-in field [:attrs :value])]
      (is (= "Testing 2" value)))))
