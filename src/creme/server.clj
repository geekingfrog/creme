(ns creme.server
  (:require [org.httpkit.server :as hk-server]
            [integrant.core :as ig]
            [creme.config :as config]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.nested-params :refer [wrap-nested-params parse-nested-keys]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn foo [param-name]
  (println "----------  param name parsing nested?" param-name)
  (parse-nested-keys param-name))

(defn start [handler port]
  (println "got opts launching server on port" port)
  (let [app (-> handler
                (wrap-resource "")
                wrap-keyword-params
                wrap-nested-params
                wrap-params)]
    {::stop-server (hk-server/run-server app {:port port})}))

(defmethod ig/init-key
  ::server
  [_ {:keys [handler config]}]
  (start handler (::config/port config)))

(defmethod ig/halt-key!
  ::server
  [_ {:keys [::stop-server]}]
  (stop-server))
