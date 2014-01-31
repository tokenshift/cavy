(ns cavy.test.regression.008_assoc_field_value
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.query :as query])
  (:use clojure.test))

(def page
  (enlive/html-snippet (slurp "test/cavy/test/test-pages/008-assoc-field-value.html")))
(def form
  (first (enlive/select page [:form])))

(deftest select-with-type
  (let [fields (query/get-form-fields form)]
    (is (= ["2"] (:select fields)))
    (is (= ["Test Text"] (:textarea fields)))))
