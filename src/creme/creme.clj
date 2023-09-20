(ns creme.creme
  (:gen-class)
  (:require
   [creme.system :as system]
   [org.httpkit.server :as hk-server]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [app (fn app [req]
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body "Hello HTTP!"})]
    (hk-server/run-server app {:port 8000})))

(comment
  (system/start)
  ; (system/stop)
  (namespace ::system/blah)
  )
