(ns creme.server
  (:require [org.httpkit.server :as hk-server]
            [integrant.core :as ig]
            [creme.config :as config]))

(defn start [handler port]
  (println "got opts launching server on port" port)
  {::stop-server (hk-server/run-server handler {:port port})})

(defmethod ig/init-key
  ::server
  [_ {:keys [handler config]}]
  (start handler (::config/port config)))

(defmethod ig/halt-key!
  ::server
  [_ {:keys [::stop-server]}]
  (stop-server))
