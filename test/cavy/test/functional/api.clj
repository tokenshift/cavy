(ns cavy.test.functional.api
  (:require cavy)
  (:use clojure.test
        cavy.test.functional.session))

(defn went-to
  "Verifies that the session has navigated to the specified URL."
  [session url]
  (is (= url (:location session))))

(deftest click-link
  (testing "by text"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "relative")
        (went-to "http://example.com/link-relative.html")))
  (testing "by URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "link-relative.html")
        (went-to "http://example.com/link-relative.html")))
  (testing "by selector"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click [:ul :li [:a :.test-link]])
        (went-to "http://example.com/link-with-class.html")))
  (testing "with an absolute URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "absolute")
        (went-to "http://example.com/link-absolute.html")))
  (testing "with a protocol-relative URL"
    (-> (test-session "https://example.com/link1.html")
        (cavy/click "protocol-relative")
        (went-to "https://example.com/link.html")))
  (testing "with a fully specified URL"
    (-> (test-session "http://example.com/link1.html")
        (cavy/click "external")
        (went-to "http://example2.com/link.html"))))
