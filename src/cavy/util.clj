(ns cavy.util
  "General utility functions."
  (:require [cemerick.url :as url]))

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
