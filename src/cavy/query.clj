(ns cavy.query
  "Functions for locating things within a page/session."
  (:require [net.cgrand.enlive-html :as enlive])
  (:use cavy.util))

(defn find-button-selector
  "Returns a selector that will locate a button."
  [target]
  (if (instance? String target)
    [[:input
      (enlive/attr= :type "submit")
      (enlive/attr= :value target)]]
    target))

(defn find-form-with-button
  "Locates the form containing the specified button."
  [page target]
  (firsts (enlive/select page
                         #{[[:form (enlive/has (find-button-selector target))]]
                           (find-button-selector target)})
          #(= :form (:tag %))
          #(and (= :input (:tag %1))
                (= "submit" (get-in %1 [:attrs :type])))))

(defn find-button
  "Locates a button on the page."
  [page target]
  (first (enlive/select page (find-button-selector target))))

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

(defn get-form-fields
  "Constructs a map of the named input fields in a form."
  [form]
  (let [fields (enlive/select form [[#{:input :select :textarea}
                                     (enlive/attr? :name)]])]
    (loop [map {} [field & rest] fields]
      (if field
        (let [name (get-in field [:attrs :name])
              key (keyword name)
              val (or (get-in field [:attrs :value])
                      (enlive/text field))]
          (recur (assoc map key (conj (or (map key) []) val))
                 rest))
        map))))
