(ns cavy
  "Represents a persistent browser session that can be queried and controlled."
  (:require [cavy.session :as session]
            [cavy.util :as util]))

(defn session
  "Creates a new Cavy session."
  [& options]
  (apply session/create options))

(defn press
  "Presses a button."
  [session target]
  session)

(defn click
  "Clicks on a link."
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

(defn visit
  "Navigates to the specified URL."
  [session url & params]
  (session/request session
                   :get url
                   :query-params (apply util/to-query-params params)))
