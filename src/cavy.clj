(ns cavy
  "Represents a persistent browser session that can be queried and controlled."
  (:require [cavy.interact :as interact]
            [cavy.query :as query]
            [cavy.session :as session]
            [cavy.util :as util]))

(defn visit
  "Navigates to the specified URL."
  [session url & params]
  (session/request session
                   :get (session/absolute-url session url)
                   :query-params (apply util/to-query-params params)))

(defn session
  "Creates a new Cavy session."
  [& [url options]]
  (let [session (apply session/create options)]
    (if url (visit session url) session)))

(defn click
  "Clicks on a link."
  [session target]
  (let [page (session :page)
        link (query/find-link page target)
        url (get-in link [:attrs :href])]
    (visit session url)))

(defn press
  "Presses a button."
  [session target]
  (let [[form button] (query/find-form-with-button (session :page) target)
        fields (query/get-form-fields form)
        method (query/get-form-method form)
        action (get-in form [:attrs :action])]
    (session/request session
                     method
                     (session/absolute-url session action)
                     :form-params fields)))

(defn fill-in
  "Fills in the specified input field."
  [session target text]
  (let [page (session :page)]
    (assoc session :page (interact/set-field-value page target text))))

(defn check
  "Checks a checkbox."
  [session target]
  ; TODO
  session)

(defn uncheck
  "Unchecks a checkbox."
  [session target]
  ; TODO
  session)

(defn toggle
  "Toggles the checkbox state."
  [session target]
  ; TODO
  session)

(defn select
  "Selects options in a radio group or dropdown."
  [session target & values]
  ; TODO
  session)
