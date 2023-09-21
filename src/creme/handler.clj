(ns creme.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :as route]
            [integrant.core :as ig]
            [creme.config :as config]
            [hiccup2.core :as h]))

(defn handler [deps]
  (routes
   (GET "/" []
     (str (h/html [:h1 "Coucou world!" " " (::config/counter deps)])))
   (GET "/coucou/:nick" [nick]
     (str (h/html [:h1 "Coucou " nick])))
   (route/not-found (str (h/html [:h1 "Page not found"])))))

(defmethod ig/init-key
  ::handler
  [_ deps]
  (println "init routes with deps:" deps)
  (handler (:config deps)))
