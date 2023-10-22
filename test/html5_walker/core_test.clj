(ns html5-walker.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [html5-walker.core :as sut]))

(deftest find-nodes
  (testing "element selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <a href=\"foo\">Hi!</a>
                  <a href=\"bar\">Howdy!</a>
                </body>"
                 [:a]))
           ["foo" "bar"])))

  (testing "element.class selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <a class=\"foo\" href=\"fool\">Hi!</a>
                  <a class=\"bar\" href=\"barn\">Howdy!</a>
                </body>"
                 [:a.bar]))
           ["barn"])))

  (testing "multiple class selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <a class=\"foo baz\" href=\"fool\">Hi!</a>
                  <a class=\"bar\" href=\"barn\">Howdy!</a>
                </body>"
                 [:a.foo.baz]))
           ["fool"])))

  (testing "descendant selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                  <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                </body>"
                 [:div.bar :a]))
           ["barn"])))

  (testing "child selector does not match any descendant"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                  <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                </body>"
                 [:div.bar :> :a]))
           [])))

  (testing "child selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                  <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                  <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                </body>"
                 [:div.bar :> :div :> :a]))
           ["barn"])))

  (testing ".class only selector"
    (is (= (map #(.getAttribute % "id")
                (sut/find-nodes
                 "<body>Hello!
                  <span class=\"foo\" id=\"fool\">Hi!</a>
                  <div class=\"foo\" id=\"food\">Howdy!</a>
                  <a class=\"foo\" id=\"foot\">Howdy!</a>
                </body>"
                 [:.foo]))
           ["fool" "food" "foot"])))

  (testing "element#id selector"
    (is (= (map #(.getAttribute % "id")
                (sut/find-nodes
                 "<body>Hello!
                  <span class=\"foo\" id=\"fool\">Hi!</a>
                  <div class=\"foo\" id=\"food\">Howdy!</a>
                  <a class=\"foo\" id=\"foot\">Howdy!</a>
                </body>"
                 [:span#fool]))
           ["fool"])))

  (testing "#id only selector"
    (is (= (map #(.getAttribute % "id")
                (sut/find-nodes
                 "<body>Hello!
                  <span class=\"foo\" id=\"fool\">Hi!</a>
                  <div class=\"foo\" id=\"food\">Howdy!</a>
                  <a class=\"foo\" id=\"foot\">Howdy!</a>
                </body>"
                 [:#fool]))
           ["fool"])))

  (testing "element:first-child selector"
    (is (= (map #(.getAttribute % "id")
                (sut/find-nodes
                 "<body><span class=\"foo\" id=\"fool\">Hi!</span></body>"
                 [:span:first-child]))
           ["fool"])))

  (testing ":first-child only selector"
    (is (= (map #(.getTagName %)
                (sut/find-nodes
                 "<body><span class=\"foo\" id=\"fool\"></span></body>"
                 [":first-child"]))
           ["HTML" "HEAD" "SPAN"])))

  (testing ":first-child combined with :last-child"
    (is (= (map #(.getTagName %)
                (sut/find-nodes
                 "<body><span class=\"foo\" id=\"fool\"></span></body>"
                 [":first-child:last-child"]))
           ["HTML" "SPAN"])))

  (testing "attribute selector"
    (is (= (map #(.getAttribute % "content")
                (sut/find-nodes
                 "<head>
                    <title>A sample blog post | Rubberduck</title>
                    <meta charset=\"utf-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <meta property=\"og:description\" content=\"A short open graph description\">
                    <meta property=\"og:title\" content=\"A sample blog post\">
                  </head>"
                 ["[property]"]))
           ["A short open graph description"
            "A sample blog post"])))

  (testing "attribute= selector"
    (is (= (map #(.getAttribute % "content")
                (sut/find-nodes
                 "<head>
                    <title>A sample blog post | Rubberduck</title>
                    <meta charset=\"utf-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <meta property=\"og:description\" content=\"A short open graph description\">
                    <meta property=\"og:title\" content=\"A sample blog post\">
                  </head>"
                 ["[property=og:title]"]))
           ["A sample blog post"])))

  (testing "attribute*= selector"
    (is (= (map #(.getAttribute % "content")
                (sut/find-nodes
                 "<head>
                    <title>A sample blog post | Rubberduck</title>
                    <meta charset=\"utf-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <meta property=\"og:description\" content=\"A short open graph description\">
                    <meta property=\"og:title\" content=\"A sample blog post\">
                  </head>"
                 ["[property*=title]"]))
           ["A sample blog post"])))

  (testing "attribute$= selector"
    (is (= (map #(.getAttribute % "content")
                (sut/find-nodes
                 "<head>
                    <title>A sample blog post | Rubberduck</title>
                    <meta charset=\"utf-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <meta property=\"og:description\" content=\"A short open graph description\">
                    <meta property=\"og:title\" content=\"A sample blog post\">
                  </head>"
                 ["[property$=title]"]))
           ["A sample blog post"])))

  (testing "attribute^= selector"
    (is (= (map #(.getAttribute % "content")
                (sut/find-nodes
                 "<head>
                    <title>A sample blog post | Rubberduck</title>
                    <meta charset=\"utf-8\">
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                    <meta property=\"og:description\" content=\"A short open graph description\">
                    <meta property=\"og:title\" content=\"A sample blog post\">
                  </head>"
                 ["[property^=og:]"]))
           ["A short open graph description"
            "A sample blog post"])))

  (testing "attribute~= selector"
    (is (= (map #(.getTextContent %)
                (sut/find-nodes
                 "<body>
                   <div class=\"button special\">One</div>
                   <div class=\"butt bodypart\">Two</div>
                  </body>"
                 ["[class~=butt]"]))
           ["Two"]))))

(deftest replace-in-document
  (is (= (sut/replace-in-document
          "<body>Hello!
             <a class=\"foo\" href=\"fool\">Hi!</a>
             <a class=\"bar\" href=\"barn\">Howdy <span class=\"first-name-holder\">first-name-goes-here</span>?</a>
           </body>"

          {[:a] (fn [node] (.setAttribute node "href" "http://example.com"))
           [:span.first-name-holder] (fn [node] (.setInnerHTML node "Arthur B Ablabab"))})
         "<html><head></head><body>Hello!
             <a class=\"foo\" href=\"http://example.com\">Hi!</a>
             <a class=\"bar\" href=\"http://example.com\">Howdy <span class=\"first-name-holder\">Arthur B Ablabab</span>?</a>
           </body></html>")))

(deftest replace-in-fragment
  (is (= (sut/replace-in-fragment
          "<div>Hello!
             <a class=\"foo\" href=\"fool\">Hi!</a>
             <a class=\"bar\" href=\"barn\">Howdy <span class=\"first-name-holder\">first-name-goes-here</span>?</a>
           </div>"

          {[:a] (fn [node] (.setAttribute node "href" "http://example.com"))
           [:span.first-name-holder] (fn [node] (.setInnerHTML node "Arthur B Ablabab"))})
         "<div>Hello!
             <a class=\"foo\" href=\"http://example.com\">Hi!</a>
             <a class=\"bar\" href=\"http://example.com\">Howdy <span class=\"first-name-holder\">Arthur B Ablabab</span>?</a>
           </div>")))
