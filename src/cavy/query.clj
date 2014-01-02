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

(defn find-by-label-selector
  "Returns a selector that will locate an element by its label text."
  [page label-text]
  (if-let [label (find-label page label-text)]
    (if-let [label-for (get-in label [:attrs :for])]
      [(enlive/id= label-for)]
      (if-let [label-id (get-in label [:attrs :id])]
        [(enlive/attr= :aria-labelledby label-id)]
        nil))
    [(enlive/attr= :aria-label label-text)]))

(defn find-by-label
  "Locates an element by its label text."
  [page label-text]
  (first (enlive/select page (find-by-label-selector page label-text))))

(defn find-link
  "Locates a link within the page."
  [page target]
  (if (instance? String target)
    (first (enlive/select page [[:a #{(enlive/attr= :href target)
                                      (enlive/has [(enlive/text-pred #(= target %))])}]]))
    (first-where-map (enlive/select page target)
                     :tag #(= :a %))))
