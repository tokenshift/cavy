(ns cavy.drivers
  "Built-in Ring and HTTP drivers."
  (:require [cavy]
            [cavy.drivers.http :as http]))

(def http
  "A driver that simulates a browser session using raw HTTP requests/responses."
  (reify cavy.Driver
    (create-session [this options]
      (http/create-session options))))
