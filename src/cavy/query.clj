(ns cavy.query
  "Functions for locating things within a page/session."
  (:require [clojure.string :as string]
            [net.cgrand.enlive-html :as enlive])
  (:use cavy.util))

(defn upper-case
  "Safe uppercase (handles nil)."
  [val]
  (if val (string/upper-case val) nil))

(defn find-button-selector
  "Returns a selector that will locate a button."
  [target]
  (cond
    (= "Submit" target)
    [[:input (enlive/attr= :type "submit") (enlive/but (enlive/attr? :value))]]

    (instance? String target)
    [[:input (enlive/attr= :type "submit") (enlive/attr= :value target)]]

    :else
    target))

(defn find-form-with-button
  "Locates the form containing the specified button."
  [page target]
  (let [selector (find-button-selector target)]
    (firsts (enlive/select page #{[[:form (enlive/has selector)]] selector})
            #(= :form (:tag %))
            #(and (= :input (:tag %1))
                  (= "submit" (get-in %1 [:attrs :type]))))))

(defn find-button
  "Locates a button on the page."
  [page target]
  (first (enlive/select page (find-button-selector target))))

(defn label-text-pred
  [label-text]
  (enlive/pred #(if-let [text (enlive/text %)]
                   (.startsWith (.trim text) label-text))))

(defn find-label
  "Locates a label with the specified text."
  [page text]
  (first (enlive/select page [[:label (label-text-pred text)]])))

(defn find-by-label-selector
  "Returns a selector that will locate an element by its label text."
  [page label-text & [and-pred]]

  (let [label (find-label page label-text)
        label-for (get-in label [:attrs :for])
        label-id (get-in label [:attrs :id])
        or-preds [(when label-for (enlive/id= label-for))
                  (when label-id (enlive/attr= :aria-labelledby label-id))
                  (enlive/attr= :aria-label label-text)]
        or-preds (set (keep identity or-preds))]
    (if and-pred [[or-preds and-pred]] [or-preds])))

(defn find-by-label
  "Locates an element by its label text."
  [page label-text]
  (first (enlive/select page (find-by-label-selector page label-text))))

(defn find-target-selector
  "Returns a selector that locates an element by text or selector."
  [page target & [and-pred]]
  (if (instance? String target)
    (find-by-label-selector page target and-pred)
    target))

(defn find-target
  "Locates an element by text or selector."
  [page target & [and-pred]]
  (first (enlive/select page (find-target-selector page target and-pred))))

(defn find-link
  "Locates a link within the page."
  [page target]
  (if (instance? String target)
    (first (enlive/select page [[:a #{(enlive/attr= :href target)
                                      (enlive/has [(enlive/text-pred #(= target %))])}]]))
    (first-where-map (enlive/select page target)
                     :tag #(= :a %))))

(defn get-form-method
  "Gets the form submission method."
  [form]
  (case (upper-case (get-in form [:attrs :method]))
    "GET" :get
    "POST" :post
    :get))

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

(defn find-radiogroup-selector
  "Returns a selector that will find a collection of radio buttons."
  [target]
  #{[[:fieldset
      (enlive/has [[:legend (label-text-pred target)]])]
     [:input (enlive/attr= :type "radio")]]
    [[:input
      (enlive/attr= :type "radio")
      (enlive/attr= :name target)]]})

(defn find-radiogroup
  "Locates a collection of radio buttons."
  [page target]
  (enlive/select page (find-radiogroup-selector target)))

(defn find-label-for-element
  "Finds the label (text) associated with the element."
  [page el]
  (let [labelledby (-> el :attrs :aria-labelledby)
        label (-> el :attrs :aria-label)
        id (-> el :attrs :id)]
    (cond
      labelledby
      (enlive/text (first (enlive/select page [[:label (enlive/attr= :id labelledby)]])))

      label
      label

      id
      (enlive/text (first (enlive/select page [[:label (enlive/attr= :for id)]])))

      :else
      nil)))
