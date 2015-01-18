(ns cavy.cookies
  "Cookie stores and utility functions.")

(declare update-store)

(deftype CookieStore [data on-update]
  clojure.lang.IPersistentMap
  (assoc [_ k v]
    (update-store (.assoc data k v) on-update))
  (assocEx [_ k v]
    (update-store (.assocEx data k v) on-update))
  (without [_ k]
    (update-store (.without data k) on-update))

  java.lang.Iterable
  (iterator [this]
    (.iterator data))


  clojure.lang.Associative
  (containsKey [_ k]
    (.containsKey data k))
  (entryAt [_ k]
    (.entryAt data k))

  clojure.lang.IPersistentCollection
  (count [_]
    (.count data))
  (cons [_ o]
    (.cons data o))
  (empty [_]
    (.empty data))
  (equiv [_ o]
    (and (isa? (class o) CookieStore)
         (.equiv data o)))

  clojure.lang.Seqable
  (seq [_]
    (.seq data))

  clojure.lang.ILookup
  (valAt [_ k]
    (.valAt data k))
  (valAt [_ k not-found]
    (.valAt data k not-found)))

(defn- update-store
  [data on-update]
  (when on-update
    (on-update data))
  (CookieStore. data on-update))


;; An in-memory cookie store.
(defn mem-store
  [& init]
  (CookieStore. (apply hash-map init) nil))
