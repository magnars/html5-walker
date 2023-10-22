(ns html5-walker.walker-test
  (:require [clojure.test :refer [deftest is testing]]
            [html5-walker.walker :as sut]))

(deftest selector-test
  (testing "implicit descendant selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                    <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                    <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                  </body>"
                 '[div.bar a]))
           ["barn"])))

  (testing "child selector does not match any descendant"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                    <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                    <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                  </body>"
                 '[div.bar > a]))
           [])))

  (testing "explicit child selector"
    (is (= (map #(.getAttribute % "href")
                (sut/find-nodes
                 "<body>Hello!
                    <div class=\"foo\"><a href=\"fool\">Hi!</a></div>
                    <div class=\"bar\"><div><a href=\"barn\">Howdy!</a></div></div>
                  </body>"
                 '[div.bar > div > a]))
           ["barn"]))))
