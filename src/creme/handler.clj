(ns creme.handler
  (:require [compojure.core :refer [GET POST] :as compojure]
            [compojure.route :as route]
            [integrant.core :as ig]
            [creme.config :as config]
            [hiccup2.core :as h]
            [taoensso.timbre :as timbre]
            [creme.coucou.form :as formtest]
            [clojure.pprint :as pp]
            [creme.org.views :as org-view]))

(defn handler [deps]
  (compojure/routes
   (GET "/" []
     (str (h/html [:h1 "Coucou world!" " " (::config/counter deps)])))
   (GET "/coucou/:nick" [nick]
     (str (h/html [:h1 "Coucou " nick])))

   (compojure/context "/org" []
     (GET "/" [] (org-view/render-create nil)) ; TODO this should be a list of org
     (POST "/" request (org-view/render-create (:params request))))

   (compojure/context "/formtest" []
     (GET "/" [] (formtest/render-page))
     (POST "/" request
       ; (timbre/debug "got a post with request" request)
       ; (pp/pprint request)
       (timbre/debug "form params:" (:form-params request))
       (timbre/debug "params:" (:params request))
       (formtest/render-page (:params request))))

   (route/not-found (str (h/html [:h1 "Page not found"])))))

(defmethod ig/init-key
  ::handler
  [_ deps]
  (handler (:config deps)))
