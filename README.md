# Cavy

[![Build Status](https://travis-ci.org/tokenshift/cavy.png?branch=develop)](https://travis-ci.org/tokenshift/cavy)

Headless Clojure HTTP client for automated testing.

## Overview

**Cavy** is a library for simulating an HTTP client without the overhead of
browser automation tools. It wraps [clj-http](https://github.com/dakrone/clj-http)
in a user-interaction-oriented interface with functions for following links,
filling in fields and submitting forms.

If you want a library for testing [Ring](https://github.com/ring-clojure/ring)
apps, I recommend [Kerodon](https://github.com/xeqi/kerodon).

## Examples

    (require 'cavy)
    
    (-> (cavy/session "https://example.com/login")
        (cavy/fill-in "Username" "my-username")
        (cavy/fill-in "Password" "my-password")
        (cavy/press "Login"))

Cookies are maintained within a session, so you can login and then navigate
secured pages.

## API

* `session [url] [options]`  
  Creates a new Cavy session. If a URL is specified, the session will
  immediately navigate there.
* `visit {url} [params...]`  
  Navigates to the specified URL. Params should be alternating key-value pairs,
  and will be appended to the URL as query parameters (`visit` always makes a
  GET request).
* `click {target}`  
  Clicks on a link specified by text or target URL.
* `press {target}`  
  Presses a button specified by text. This should be a 'submit' button in a
  form, and will provoke a POST request with any form data.
* `fill-in {target} {text}`  
  Fills in a textbox (or password box, or textarea) with the specified text.
* `check {target}`, `uncheck {target}`, `toggle {target}`  
  Checks, unchecks or toggles the specified checkbox.
* `select {target} [values...]`  
  Selects the specified options in a dropdown. If no values are given, all
  options will be deselected.
* `choose {target} {value}`  
  Selects the specified option in a group of radio buttons.
