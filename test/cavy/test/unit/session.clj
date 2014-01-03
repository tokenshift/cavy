(ns cavy.test.unit.session
  (:require [cavy.session :as session])
  (:use clojure.test))

(deftest merge-cookies
  (testing "no domain specified"
    (let [cookies (session/merge-cookies "http://www.example.com/test/foo"
                                         {} {"test1" "FooBar"
                                             "test2" "FizzBuzz"})]
      (is (= {"www.example.com" {"testing" "FizzBuzz"}} cookies)))))
