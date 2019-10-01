(ns html5-walker.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [html5-walker.core :as sut]))

(deftest find-nodes
  (is (= (map #(.getAttribute % "href")
              (sut/find-nodes
               "<body>Hello!
                  <a href=\"foo\">Hi!</a>
                  <a href=\"bar\">Howdy!</a>
                </body>"
               [:a]))
         ["foo" "bar"]))

  (is (= (map #(.getAttribute % "href")
              (sut/find-nodes
               "<body>Hello!
                  <a class=\"foo\" href=\"fool\">Hi!</a>
                  <a class=\"bar\" href=\"barn\">Howdy!</a>
                </body>"
               [:a.bar]))
         ["barn"]))

  (is (= (map #(.getAttribute % "href")
              (sut/find-nodes
               "<body>Hello!
                  <a class=\"foo baz\" href=\"fool\">Hi!</a>
                  <a class=\"bar\" href=\"barn\">Howdy!</a>
                </body>"
               [:a.foo.baz]))
         ["fool"]))

  (is (= (map #(.getAttribute % "href")
              (sut/find-nodes
               "<body>Hello!
                  <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                  <div class=\"bar\"><a href=\"barn\">Howdy!</a></div>
                </body>"
               [:div.bar :a]))
         ["barn"])))

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
