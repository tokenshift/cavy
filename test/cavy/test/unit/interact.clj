(ns cavy.test.unit.interact
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.interact :as interact])
  (:use clojure.test))

(defn unchanged
  "Verifies that two versions of the page are identical."
  [el1 el2]
  (cond
    (instance? clojure.lang.Counted el1)
    (if (= (count el1) (count el2))
      (every? identity (map unchanged el1 el2))
      false)

    (map? el1)
    (if (map? el2)
      (every? identity (map (fn [[k v]] (= v (el2 k))) el1))
      false)

    :else
    (= el1 el2)))

(def query-tests
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

(deftest set-checked
  (testing "checked = true"
    (let [result (interact/set-checked query-tests "Field 5" true)
          field (first (enlive/select result [:#field5]))]
      (is (= "checked" (get-in field [:attrs :checked])))))
  (testing "checked = false"
    (let [result (interact/set-checked query-tests "Field 5" false)
          field (first (enlive/select result [:#field5]))]
      (is (nil? (get-in field [:attrs :checked])))
      (is (not (contains? (field :attrs) :checked)))))
  (testing "checked = toggle"
    (let [result1 (interact/set-checked query-tests "Field 5" :toggle)
          result2 (interact/set-checked result1 "Field 5" :toggle)
          field1 (first (enlive/select result1 [:#field5]))
          field2 (first (enlive/select result2 [:#field5]))]
      (is (= "checked" (get-in field1 [:attrs :checked])))
      (is (nil? (get-in field2 [:attrs :checked])))))
  (testing "checkbox not found"
    (let [result (interact/set-checked query-tests "Whatever" :toggle)]
      (is (unchanged result query-tests)))))

(deftest set-field-value
  (testing "by label"
    (let [result (interact/set-field-value query-tests "Field 1" "Testing")
          field (first (enlive/select result [:#field1]))
          value (get-in field [:attrs :value])]
      (is (= "Testing" value))))
  (testing "by selector"
    (let [result (interact/set-field-value query-tests [:#field1] "Testing 2")
          field (first (enlive/select result [:#field1]))
          value (get-in field [:attrs :value])]
      (is (= "Testing 2" value))))
  (testing "field not found"
    (let [result (interact/set-field-value query-tests "Whatever" "Testing")]
      (is (unchanged result query-tests)))))
