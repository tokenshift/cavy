(ns cavy.interact
  "Functions for interacting with a Cavy session."
  (:require [net.cgrand.enlive-html :as enlive]
            [cavy.query :as query]))

(defn- set-value
  "Enlive element transformation to set the value of an element."
  [value]
  (fn [element]
    (assoc-in element [:attrs :value] value)))

(defn set-field-value
  "Sets the text value of an input field."
  [page target text]
  (if (instance? String target)
    (enlive/transform page
                      (query/find-by-label-selector page target)
                      (set-value text))
    (enlive/transform page
                      target
                      (set-value text))))
