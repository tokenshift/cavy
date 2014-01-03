(ns cavy.test.unit.util
  (:require [cavy.util :as util])
  (:use clojure.test))

(deftest firsts
  (let [result (util/firsts [{:a true :b false :c false}
                             {:a false :b false :c false}
                             {:a true :b true :c false}
                             {:a false :b false :c false}]
                            :a :b)]
    (is (= {:a true :b false :c false} (nth result 0)))
    (is (= {:a true :b true :c false} (nth result 1)))))
