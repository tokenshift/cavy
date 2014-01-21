(ns cavy.test.functional.api
  (:require [net.cgrand.enlive-html :as enlive]
            cavy)
  (:use clojure.test
        cavy.test.utils
        cavy.test.functional.session))

(defmacro with-session
  "Helper to thread a session through an arbitrary form."
  [session name & forms]
  `((fn [~name] ~@forms ~name) ~session))

(defn went-to
  "Verifies that the session has navigated to the specified URL."
  [session url]
  (is (= url (:location session)))
  session)

(deftest click
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
        (went-to "http://example2.com/link.html")))
  (testing "link not found"
    (let [session (test-session "http://example.com/link1.html")
          result (cavy/click session"whatever")]
      (is (unchanged result session)))))

(deftest fill-in
  (testing "login fields"
    (let [result (-> (test-session "http://example.com/login.html")
                     (cavy/fill-in "Username" "test-user")
                     (cavy/fill-in "Password" "test-password"))
          page (result :page)
          username (first (enlive/select page [:#username]))
          password (first (enlive/select page [:#password]))]
      (is (= "test-user" (get-in username [:attrs :value])))
      (is (= "test-password" (get-in password [:attrs :value])))))
  (testing "field not found"
    (let [session (test-session "http://example.com/login.html")
          result (cavy/fill-in session "Whatever" "testing")]
      (is (unchanged result session)))))

(deftest press
  (testing "submitting login form"
    (let [result (-> (test-session "http://example.com/login.html")
                     (cavy/fill-in "Username" "test-username")
                     (cavy/fill-in "Password" "test-password")
                     (cavy/fill-in "Description" "Lorem ipsum...")
                     (cavy/press "Login"))
          request @(result :last-request)]
      (is (= :post (request :method)))
      (is (= "http://example.com/login" (request :url)))
      (is (not (nil? (get-in request [:options :form-params]))))
      (is (= ["test-username"] (get-in request [:options :form-params :username])))
      (is (= ["test-password"] (get-in request [:options :form-params :password])))))
  (testing "button not found"
    (let [session (test-session "http://example.com/login.html")
          result (cavy/press session "Whatever")]
      (is (unchanged result session)))))

(deftest form-submit
  (testing "all form fields"
    (let [result (-> (test-session "http://example.com/form2.html")
                     (cavy/fill-in "Field 1" "Text Box Test")
                     (cavy/fill-in "Password" "This is my password")
                     (cavy/check "Selected")
                     (cavy/fill-in "Description" "Lorem ipsum dolor sit amet...")
                     (cavy/select "Single Select" "Option 3")
                     (cavy/select "Multi Select" "Option 2" "Option 4" "Option 6")
                     (cavy/choose "Select One" "Option 2")
                     (cavy/choose "radiogroup2" "Option 3")
                     (cavy/press "No"))
          request @(result :last-request)
          params (-> request :options :form-params)]
      (is (not (nil? request)))
      (is (not (nil? params)))
      (is (= ["Testing Hidden Fields"] (params :hidden)))
      (is (= ["Text Box Test"] (params :field1)))
      (is (= ["This is my password"] (params :password)))
      (is (= ["Selected"] (params :checkbox)))
      (is (= ["Lorem ipsum dolor sit amet..."] (params :description)))
      (is (= ["3"] (params :singleselect)))
      (is (= (hash-set "2" "4" "6") (apply hash-set (params :multiselect))))
      (is (= ["2"] (params :radiogroup1)))
      (is (= ["3"] (params :radiogroup2)))
      (is (= ["No"] (params :action))))))
