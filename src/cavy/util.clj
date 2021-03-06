(ns cavy.util
  "General utility functions."
  (:require [cemerick.url :as url]))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn firsts
  "Selects the first elements of a sequence that satisfy a list of predicates."
  [seq & preds]
  (let [found (loop [[head & rest] seq
                     preds-map (into {} (map-indexed (fn [k v] [k v]) preds))
                     found-map {}]
                (if head
                  (recur rest
                         preds-map
                         (into {} (map (fn [[k pred]]
                                         [k (or (found-map k)
                                                (when (pred head) head))])
                                       preds-map)))
                  found-map))]
    (map-indexed (fn [k _] (found k)) preds)))


(defn first-where-map
  "Selects the first element of a sequence where the mapped value matches the
  predicate."
  [seq fun pred]
  (first
    (filter
      #(pred (fun %))
      seq)))

(defn in-str?
  "Checks whether a string contains the specified text."
  [string text]
  (when string
    (if (instance? String string)
      (.contains string text)
      (throw (IllegalArgumentException. "Input was not a string.")))))

(defn to-query-params
  "Converts a list of key-value pairs into a query parameters map."
  ([] {})
  ([k v & rest]
   (let [key (url/url-encode (name k))
         val (url/url-encode v)
         qry (apply to-query-params rest)]
     (assoc qry key (if (qry key) (conj (qry key) val) [val])))))
