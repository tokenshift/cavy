(ns cavy.test.unit.interact
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.interact :as interact])
  (:use clojure.test))

(def page
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/query-tests.html")))

(deftest set-value
  (testing "by label"
    (let [result (interact/set-value page "Field 1" "Testing")
          field (enlive/select result [:#field1])
          value (get-in field [:attrs :value])]
      (is (= "Testing" value))))
  (testing "by selector"
    (let [result (interact/set-value page
                                     [[:input (enlive/attr= :aria-label "Field 3")]]
                                     "Testing 2")
          field (enlive/select result [:#field1])
          value (get-in field [:attrs :value])]
      (is (= "Testing 2" value)))))
