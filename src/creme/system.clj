(ns creme.system
  (:require [integrant.core :as ig]
            [creme.server :as server]
            [creme.handler :as handler]
            [creme.config :as config]))

; TODO: check https://www.pixelated-noise.com/blog/2022/04/28/integrant-and-aero/index.html
; to have aero (https://github.com/juxt/aero) for configuration + integrant
(def config
  {::handler/handler {:config (ig/ref ::config/config)}
   ::server/server {:handler (ig/ref ::handler/handler)
                    :config (ig/ref ::config/config)}
   ::config/config {}})

(defn start
  []
  (println "starting system")
  (ig/init config))

(defn stop
  [system]
  (println "shutting down system")
  (ig/halt! system))
