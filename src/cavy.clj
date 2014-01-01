(ns cavy
  "Represents a persistent browser session that can be queried and controlled."
  (:require [cavy.session :as session]
            [cavy.util :as util]))

(defn session
  "Creates a new Cavy session."
  [& options]
  (apply session/create options))

(defn click-button
  "Clicks on a button."
  [session name]
  session)

(defn click-link
  "Clicks on a link."
  [session name]
  session)

(defn click
  "Clicks on a link or button."
  [session name]
  session)

(defn fill-in
  "Fills in the specified input field."
  [session label value]
  session)

(defn check
  "Checks a checkbox."
  [session name]
  session)

(defn uncheck
  "Unchecks a checkbox."
  [session name]
  session)

(defn toggle
  "Toggles the checkbox state."
  [session name]
  session)

(defn select
  "Selects options in a radio group or dropdown."
  [session name & values]
  session)

(defn visit
  "Navigates to the specified URL."
  [session url & params]
  (session/request session
                   :get url
                   :query-params (apply util/to-query-params params)))
