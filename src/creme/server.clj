(ns creme.server
  (:require [org.httpkit.server :as hk-server]
            [integrant.core :as ig]))

(defn start [opts]
  (println "got opts launching server" opts)
  (let [app (fn app [_req]
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body "Hello HTTP!"})]
    {::stop-server (hk-server/run-server app opts)}))

(defmethod ig/init-key
  ::server
  [_ opts]
  (start opts))

(defmethod ig/halt-key!
  ::server
  [_ {:keys [::stop-server] :as opts}]
  (println "halting server with opts" opts)
  (stop-server))
