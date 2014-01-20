(ns cavy.interact
  "Functions for interacting with a Cavy session."
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.query :as query])
  (:use cavy.util))

(defn- transform-checked
  "Enlive element transformation to check/uncheck a checkbox."
  [value]
  (fn [el]
    (let [check (if (= :toggle value)
                  (not (contains? (el :attrs) :checked))
                  value)]
      (if check
        (assoc-in el [:attrs :checked] "checked")
        (dissoc-in el [:attrs :checked])))))

(defn- set-option-value
  "Marks the option as selected if its value is in the list of values."
  [values]
  (fn [option]
    (if (some #(or (= (-> option :attrs :value) %)
                   (= (enlive/text option) %))
              values)
      (assoc-in option [:attrs :selected] "selected")
      (assoc option :attrs (dissoc (option :attrs) :selected)))))

(defn- select-options
  "Transformation to select a set of options in a dropdown."
  [& values]
  (fn [el]
    (if (-> el :attrs :multiple)
      (enlive/at el [:option] (set-option-value values))
      (enlive/at el [:option] (set-option-value (first values))))))

(defn- set-value
  "Enlive element transformation to set the value of an element."
  [value]
  (fn [element]
    (assoc-in element [:attrs :value] value)))

(defn choose
  "Selects an option in a radio group."
  [page target value]
  ; TODO
  page)

(defn select
  "Selections options in a dropdown."
  [page target & values]
  (enlive/transform page (query/find-target-selector page target [:select])
                    (select-options values)))

(defn set-checked
  "Sets whether a checkbox is checked."
  [page target checked]
  (if (instance? String target)
    (enlive/transform page
                      (query/find-by-label-selector
                        page target
                        [:input (enlive/attr= :type "checkbox")])
                      (transform-checked checked))
    (enlive/transform page
                      target
                      (transform-checked checked))))

(defn set-field-value
  "Sets the value of an input field."
  [page target text]
  (if (instance? String target)
    (enlive/transform page
                      (query/find-by-label-selector page target)
                      (set-value text))
    (enlive/transform page
                      target
                      (set-value text))))
