(ns html5-walker.core
  (:require [clojure.string :as str])
  (:import (ch.digitalfondue.jfiveparse Element Parser Selector)))

(def prefix->kind
  {nil :element
   "#" :id
   "." :class
   "[" :attr})

(defn parse-selector
  "Breaks a CSS selector element into tag matcher, class matchers, id matcher, and
  attribute matchers."
  [selector]
  (->> (str/replace selector #":(first|last)-child" "")
       (re-seq #"([#\.\[])?([a-z0-9\-\:]+)(?:(.?=)([a-z0-9\-\:]+)])?")
       (map #(into [(prefix->kind (second %))] (remove nil? (drop 2 %))))
       (concat (->> (re-seq #":((?:first|last)-child)" selector)
                    (map (comp vector keyword second))))))

(comment

  (parse-selector "div#content.text[property=og:image].mobile[style][data-test~=bla]:first-child")
  (parse-selector "div:first-child")
  (parse-selector ":first-child")
  (parse-selector "[property]")

  )

(defn- match-path-fragment [selector element]
  (reduce
   (fn [s [kind m comparator v]]
     (case kind
       :element (.element s m)
       :id (.id s m)
       :class (.hasClass s m)
       :attr (case comparator
               "=" (.attrValEq s m v)
               "*=" (.attrValContains s m v)
               "$=" (.attrValEndWith s m v)
               "~=" (.attrValInList s m v)
               "^=" (.attrValStartWith s m v)
               nil (.attr s m))
       :first-child (.isFirstChild s)
       :last-child (.isLastChild s)))
   selector
   (parse-selector (name element))))

(defn create-matcher [path]
  (.toMatcher
   (reduce (fn [selector element-kw]
             (-> selector
                 .withChild
                 (match-path-fragment element-kw)))
           (-> (Selector/select)
               (match-path-fragment (first path)))
           (next path))))

(defn replace-in-document [html path->f]
  (let [parser (Parser.)
        doc (.parse parser html)]
    (doseq [[path f] path->f]
      (doseq [node (.getAllNodesMatching doc (create-matcher path))]
        (f node)))
    (.getOuterHTML (.getDocumentElement doc))))

(defn replace-in-fragment [html path->f]
  (let [parser (Parser.)
        el (first (.parseFragment parser (Element. "div") (str "<div>" html "</div>")))]
    (doseq [[path f] path->f]
      (doseq [node (.getAllNodesMatching el (create-matcher path))]
        (f node)))
    (.getInnerHTML el)))

(defn find-nodes [html path]
  (.getAllNodesMatching (.parse (Parser.) html) (create-matcher path)))
