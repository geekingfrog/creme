(ns creme.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :as route]
            [integrant.core :as ig]
            [creme.config :as config]))

(defn handler [deps]
  (routes
   (GET "/" [] (format "<h1>Coucou world %d </h1>" (::config/counter deps)))
   (GET "/coucou/:nick" [nick]
     (format "<h1>Coucou %s</h1>" nick))
   (route/not-found "<h1>Page not found</h1>")))

(defmethod ig/init-key
  ::handler
  [_ deps]
  (println "init routes with deps:" deps)
  (handler (:config deps)))
