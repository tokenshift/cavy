(ns cavy.query
  "Functions for locating things within a page/session."
  (:require [net.cgrand.enlive-html :as enlive])
  (:use cavy.util))

(defn find-label
  "Locates a label with the specified text."
  [page text]
  (first (enlive/select
           page
           [[:label
             (enlive/has
               [(enlive/text-pred #(.startsWith (.trim %) text))])]])))
  

(defn find-link
  "Locates a link within the page."
  [page target]
  (if (instance? String target)
    (first (enlive/select page [[:a #{(enlive/attr= :href target)
                                      (enlive/has [(enlive/text-pred #(= target %))])}]]))
    (first-where-map (enlive/select page target)
                     :tag #(= :a %))))
