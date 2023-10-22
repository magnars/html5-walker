(ns html5-walker.core
  (:require [html5-walker.walker :as walker]))

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

(defn ^:export replace-in-document [html path->f]
  (->> (for [[path f] path->f]
         [(enforce-child-selectors path) f])
       (into {})
       (walker/replace-in-document html)))

(defn ^:export replace-in-fragment [html path->f]
  (->> (for [[path f] path->f]
         [(enforce-child-selectors path) f])
       (into {})
       (walker/replace-in-fragment html)))

(defn ^:export find-nodes [html path]
  (walker/find-nodes html (enforce-child-selectors path)))
