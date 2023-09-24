(ns creme.config
  (:require [integrant.core :as ig]
            [taoensso.timbre :as timbre]))

(defmethod ig/init-key
  ::config
  [_ _]
  (do
    (timbre/merge-config!
     {:min-level
      [["com.zaxxer.hikari" :warn]
       ["creme.*" :debug]
       ["*" :info]]})
    {::port 8000
     ::counter 3
     ::db {::name "creme"
           ::username "creme"
           ::socket-address "/var/run/postgresql/.s.PGSQL.5432"}}))
