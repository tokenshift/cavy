(ns cavy
  "Front-end/API for all drivers.")

(defprotocol Driver
  "A driver is an implementation of the Cavy API that wraps a (real, headless,
  or simulated) browser session."
  (create-session [this options]
  "Create a new browser session. Options are driver-specific."))

(defprotocol Session
  "A browser session returned by a specific driver; provides methods for
  inspecting and interacting with the current session/page. All modifications
  return a new session, and are chainable."

  ; Navigation
  (visit [this url] "Navigate to the specified URL.")

  ; Response Info
  (body [this] "Return the HTML body.")
  (headers [this] "Return a map of response headers.")
  (path [this] "Return the current page path.")
  (query [this] "Return the query string from the current URL.")
  (status [this] "Return the status code of the last response.")
  (text [this] "Return all page text.")
  (url [this] "Return the current page URL.")

  ; Finders/Inspection
  (elements [this selector] "Return all elements matching the selector.")

  ; Interaction
  (click [this selector] "Click on a link.")
  (press [this selector] "Press a button.")
  (fill-in [this selector text] "Fill in a text field.")
  (check [this selector] "Check a checkbox.")
  (uncheck [this selector] "Uncheck a checkbox.")
  (toggle [this selector] "Toggle a checkbox.")
  (select [this selector & values] "Select options in a dropdown.")
  (choose [this selector value] "Select an option in a radio group."))

(defn session
  "Create a new Cavy session using the specified driver."
  [driver & [options]]
  (create-session driver (or options {})))
