(ns html5-walker.core
  (:require [html5-walker.walker :as walker])
  (:import (ch.digitalfondue.jfiveparse Element Parser)))

(defn enforce-child-selectors
  "Preserves the old behavior where [:div :a] enforced a child relationship."
  [path]
  (->> (partition-all 2 1 path)
       (remove (comp #{">"} #(some-> % name) first))
       (mapcat
        (fn [[element descendant]]
          (if (nil? descendant)
            [element]
            [element '>])))))

(defn create-matcher [path]
  (walker/create-matcher (enforce-child-selectors path)))

(defn ^:export replace-in-document [html path->f]
  (let [doc (.parse (Parser.) html)]
    (doseq [[path f] path->f]
      (doseq [node (.getAllNodesMatching doc (create-matcher path))]
        (f node)))
    (.getOuterHTML (.getDocumentElement doc))))

(defn ^:export replace-in-fragment [html path->f]
  (let [el (first (.parseFragment (Parser.) (Element. "div") (str "<div>" html "</div>")))]
    (doseq [[path f] path->f]
      (doseq [node (.getAllNodesMatching el (create-matcher path))]
        (f node)))
    (.getInnerHTML el)))

(defn ^:export find-nodes [html path]
  (.getAllNodesMatching (.parse (Parser.) html) (create-matcher path)))
