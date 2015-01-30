(ns cavy.drivers.http
  "An HTTP-based driver (wrapping clj-http) to hit external applications and
  simulate a persistent client session without running an actual browser."
  (:require [clj-http.client :as http]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as enlive]
            [uri.core :as uri]
            [cavy]))

(defn find-button-selector
  "Returns a selector that will locate a button."
  [selector]
  (cond
    (= "Submit" selector)
    [[:input (enlive/attr= :type "submit") (enlive/but (enlive/attr? :value))]]

    (instance? String selector)
    [[:input (enlive/attr= :type "submit") (enlive/attr= :value selector)]]

    :else
    selector))

(defn get-form-method
  "Gets the form submission method."
  [form]
  (case (some-> form :attrs :method string/upper-case)
    "GET" :get
    "POST" :post
    :get))

;; Adds the value of the specified form field to a map.
(defmulti assoc-field-value
  (fn [fields field]
    [(-> field :tag)
     (if (= (-> field :tag) :input)
       (-> field :attrs :type)
       nil)]))

(defmethod assoc-field-value [:input "checkbox"]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (-> field :attrs :value)
        checked (-> field :attrs :checked)]
    (if checked
      (assoc fields name (conj (fields name) value))
      fields)))

(defmethod assoc-field-value [:input "hidden"]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (-> field :attrs :value)]
    (assoc fields name (conj (fields name) value))))

(defmethod assoc-field-value [:input "password"]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (-> field :attrs :value)]
    (assoc fields name (conj (fields name) value))))

(defmethod assoc-field-value [:input "radio"]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (-> field :attrs :value)
        checked (-> field :attrs :checked)]
    (if checked
      (assoc fields name (conj (fields name) value))
      fields)))

(defmethod assoc-field-value [:input "text"]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (-> field :attrs :value)]
    (assoc fields name (conj (fields name) value))))

(defmethod assoc-field-value [:select nil]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        selected (enlive/select field [[:option (enlive/attr? :selected)]])
        values (map #(get-in % [:attrs :value]) selected)]
    (assoc fields name (apply conj (fields name) values))))

(defmethod assoc-field-value [:textarea nil]
  [fields field]
  (let [name (-> field :attrs :name keyword)
        value (enlive/text field)]
    (assoc fields name (conj (fields name) value))))

(defmethod assoc-field-value :default
  [fields field]
  ; Do nothing
  fields)

(defn get-form-fields
  "Constructs a map of the named input fields in a form."
  [form & [button]]
  ; Get every named form field.
  (let [fields (enlive/select form [[#{:input :select :textarea}
                                     (enlive/attr? :name)]])
        ; Add each field value to a map of field values.
        field-map (reduce #(assoc-field-value %1 %2) {} fields)
        ; Add the selected button value (if it is named).
        button-name (-> button :attrs :name keyword)
        button-val (-> button :attrs :value)]
    (if (and button-name button-val)
      (assoc field-map button-name (conj (field-map button-name) button-val))
      field-map)))

(defn press-button
  [session selector]
  (let [page (::page session)
        button-selector (find-button-selector selector)
        form-and-button (enlive/select page #{[[:form (enlive/has button-selector)]] button-selector})
        form (first (enlive/select form-and-button [:form]))
        button (first (enlive/select form-and-button [[:input (enlive/attr= :type "submit")]]))
        fields (get-form-fields form button)
        method (get-form-method form)
        action (get-in form [:attrs :action])
        url (str (uri/resolve (::url session) (uri/make action)))
        response (http/request {:method method :url url})
        url (or (last (:trace-redirects response)) url)
        page (-> response :body enlive/html-snippet)]
    (-> session
        (assoc ::response response)
        (assoc ::url (uri/make url))
        (assoc ::page page))))

(defrecord Session [cookies]
  cavy/Session
  (visit [this url]
    (let [response (http/get url)
          url (or (last (:trace-redirects response)) url)
          page (-> response :body enlive/html-snippet)]
      (-> this
          (assoc ::response response)
          (assoc ::url (uri/make url))
          (assoc ::page page))))

  (body [this] (-> this ::response :body))
  (headers [this] (-> this ::response :headers))
  (path [this] (-> this ::url uri/path))
  (query [this] (-> this ::url uri/query))
  (status [this] (-> this ::response :status))
  (text [this] (->> this ::page (map enlive/text) (apply str)))
  (url [this] (-> this ::url str))

  (press [this selector] (press-button this selector)))

(defn create-session
  "Create a new HTTP driver session."
  [options]
  (let [cookies (clj-http.cookies/cookie-store)]
    (->Session cookies)))
