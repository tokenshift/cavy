(ns cavy.test.utils)

(defn unchanged
  "Verifies that two versions of the page are identical."
  [el1 el2]
  (cond
    (instance? clojure.lang.Counted el1)
    (if (= (count el1) (count el2))
      (every? identity (map unchanged el1 el2))
      false)

    (map? el1)
    (if (map? el2)
      (every? identity (map (fn [[k v]] (= v (el2 k))) el1))
      false)

    :else
    (= el1 el2)))
