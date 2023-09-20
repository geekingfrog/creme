(ns creme.system
  (:require [integrant.core :as ig]
            [creme.server :as server]))

; TODO: check https://www.pixelated-noise.com/blog/2022/04/28/integrant-and-aero/index.html
; to have aero (https://github.com/juxt/aero) for configuration + integrant
(def config
  {::server-opts {}
   ::server/server (ig/ref ::server-opts)})

(defmethod ig/init-key
  ::server-opts
  [_ _]
  {:port 8000})

(defn start
  []
  (println "starting system")
  (ig/init config))

(defn stop
  [system]
  (println "shutting down system")
  (ig/halt! system))
