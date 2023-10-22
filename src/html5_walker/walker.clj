(ns html5-walker.walker
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

(defn make-descendants-explicit
  "Walks a path and returns pairs of [descendant element] where descendant is
  either `:descendant` or `:child`, describing the desired relationship to the
  previous path element. `descendant` will be `nil` for the first element. `:>`
  creates a `:child` relationship between two elements, while elements that
  don't have an explicit relationship (e.g. `[:div :a]`) will have a
  `:descendant` interposed between them."
  [path]
  (->> (partition-all 2 1 path)
       (remove (comp #{">"} #(some-> % name) first))
       (mapcat
        (fn [[element descendant]]
          (cond
            (nil? descendant)
            [element]

            (= ">" (name descendant))
            [element :child]

            :else
            [element :descendant])))
       (into [nil])
       (partition 2)))

(defn create-matcher [path]
  (let [path (make-descendants-explicit path)]
    (.toMatcher
     (reduce (fn [selector [descendant element-kw]]
               (-> (case descendant
                     :descendant (.withDescendant selector)
                     :child (.withChild selector))
                   (match-path-fragment element-kw)))
             (-> (Selector/select)
                 (match-path-fragment (second (first path))))
             (next path)))))

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
