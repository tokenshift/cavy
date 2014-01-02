(ns cavy
  "Represents a persistent browser session that can be queried and controlled."
  (:require [cavy.query :as query]
            [cavy.session :as session]
            [cavy.util :as util]))

(defn session
  "Creates a new Cavy session."
  [& options]
  (apply session/create options))

(defn visit
  "Navigates to the specified URL."
  [session url & params]
  (session/request session
                   :get (session/absolute-url session url)
                   :query-params (apply util/to-query-params params)))

(defn click
  "Clicks on a link."
  [session target]
  (let [page (session :page)
        link (query/find-link page target)
        url (get-in link [:attrs :href])]
    (println "Link:" link "URL:" url)
    (visit session url)))

(defn press
  "Presses a button."
  [session target]
  session)

(defn fill-in
  "Fills in the specified input field."
  [session target value]
  session)

(defn check
  "Checks a checkbox."
  [session target]
  session)

(defn uncheck
  "Unchecks a checkbox."
  [session target]
  session)

(defn toggle
  "Toggles the checkbox state."
  [session target]
  session)

(defn select
  "Selects options in a radio group or dropdown."
  [session target & values]
  session)
