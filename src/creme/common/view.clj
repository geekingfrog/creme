(ns creme.common.view
  (:require [hiccup.page :refer [html5]]
            [hiccup2.core :as h]))

(defn base
  "Base view taking care of enveloppe and common includes"
  [opts]
  (html5
   (h/html
    [:head
     [:link {:rel "stylesheet" :href "/staticfiles/pico.min.css"}]
     [:link {:rel "stylesheet" :href "/staticfiles/styles.css"}]]
    [:body (:body opts)])))
