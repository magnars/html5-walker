(ns html5-walker.core
  (:require [clojure.string :as str])
  (:import (ch.digitalfondue.jfiveparse Element Parser Selector)))

(defn- match-path-fragment [selector element-kw]
  (let [[tag-name & classes] (str/split (name element-kw) #"\.")]
    (reduce
     (fn [s class] (.hasClass s class))
     (cond-> selector
       (seq tag-name)
       (.element tag-name))
     classes)))

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
