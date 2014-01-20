(ns cavy.test.unit.interact
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.interact :as interact])
  (:use clojure.test
        cavy.test.utils))

(def query-tests
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

(def form
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/form.html")))

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

(deftest select
  (testing "single-select dropdown"
    (testing "selecting by text"
      (let [result (interact/select form "Single Select" "2")
            chosen (first (enlive/select result [[:option (enlive/attr? :selected)]]))]
        (is (not (nil? chosen)))
        (is (= "2" (-> chosen :attrs :value)))))
    (testing "selecting by value"
      (let [result (interact/select form "Single Select" "Option 3")
            chosen (first (enlive/select result [[:option (enlive/attr? :selected)]]))]
        (is (not (nil? chosen)))
        (is (= "3" (-> chosen :attrs :value)))))
    (testing "deselecting"
      (let [result (interact/select form "Single Select" "3")
            result (interact/unselect result "Single Select" "3")
            chosen (first (enlive/select result [[:option (enlive/attr? :selected)]]))]
        (is (nil? chosen)))))
  (testing "multi-select dropdown"
    (testing "selecting by text"
      (let [result (interact/select form "Multi Select" "2 4 6")
            chosen (enlive/select result [[:option (enlive/attr? :selected)]])]
        (is (= 3 (count chosen)))
        (is (= "2" (get-in (nth chosen 0) [:attrs :value])))
        (is (= "4" (get-in (nth chosen 1) [:attrs :value])))
        (is (= "6" (get-in (nth chosen 2) [:attrs :value])))))
    (testing "selecting by value"
      (let [result (interact/select form "Multi Select" "Option 1" "Option 3" "Option 5")
            chosen (enlive/select result [[:option (enlive/attr? :selected)]])]
        (is (= 3 (count chosen)))
        (is (= "1" (get-in (nth chosen 0) [:attrs :value])))
        (is (= "3" (get-in (nth chosen 1) [:attrs :value])))
        (is (= "5" (get-in (nth chosen 2) [:attrs :value])))))
    (testing "deselecting"
      (let [result (interact/select form "Multi Select" "1" "3" "5")
            result (interact/unselect result "Multi Select" "3")
            chosen (enlive/select result [[:option (enlive/attr? :selected)]])]
        (is (= 2 (count chosen)))
        (is (= "1" (get-in (nth chosen 0) [:attrs :value])))
        (is (= "5" (get-in (nth chosen 1) [:attrs :value])))))))

(deftest choose
  (testing "in fieldset"
    (testing "by legend"
      (let [result (interact/choose form "Select One" "1")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "1" (get-in (nth chosen 0) [:attrs :value])))))
    (testing "by name"
      (let [result (interact/choose form "radiogroup1" "2")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "2" (get-in (nth chosen 0) [:attrs :value])))))
    (testing "by label"
      (let [result (interact/choose form "Select One" "Option 3")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "3" (get-in (nth chosen 0) [:attrs :value]))))))
  (testing "not in fieldset"
    (testing "by value"
      (let [result (interact/choose form "radiogroup2" "2")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "2" (get-in (nth chosen 0) [:attrs :value])))))
    (testing "by label"
      (let [result (interact/choose form "radiogroup2" "Option 2")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "2" (get-in (nth chosen 0) [:attrs :value])))))
    (testing "deselecting"
      (let [result (interact/choose form "radiogroup2" "2")
            result (interact/choose form "radiogroup2" nil)
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 0 (count chosen)))))
    (testing "changing"
      (let [result (interact/choose form "radiogroup2" "Option 2")
            result (interact/choose form "radiogroup2" "Option 1")
            chosen (enlive/select result [[:input (enlive/attr? :checked)]])]
        (is (= 1 (count chosen)))
        (is (= "1" (get-in (nth chosen 0) [:attrs :value])))))))
