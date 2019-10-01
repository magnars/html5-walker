(ns html5-walker.core
  (:import [ch.digitalfondue.jfiveparse Document Element Node Parser Selector NodeMatcher])
  (:require [clojure.string :as str]))

(defn- match-path-fragment [selector element-kw]
  (let [[tag-name & classes] (str/split (name element-kw) #"\.")]
    (reduce
     (fn [s class] (.hasClass s class))
     (.element selector tag-name)
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
    (.getOuterHTML doc)))

(defn replace-in-fragment [html path->f]
  (let [parser (Parser.)
        doc (first (.parseFragment parser (Element. "div") (str "<div>" html "</div>")))]
    (doseq [[path f] path->f]
      (doseq [node (.getAllNodesMatching doc (create-matcher path))]
        (f node)))
    (.getInnerHTML doc)))

(defn find-nodes [html path]
  (.getAllNodesMatching (.parse (Parser.) html) (create-matcher path)))
