(ns cavy.util
  "General utility functions."
  (:require [cemerick.url :as url]))

(defn to-query-params
  "Converts a list of key-value pairs into a query parameters map."
  ([] {})
  ([k v & rest]
   (let [key (url/url-encode (name k))
         val (url/url-encode v)
         qry (apply to-query-params rest)]
     (assoc qry key (if (qry key) (conj (qry key) val) [val])))))
