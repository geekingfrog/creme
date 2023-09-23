(ns creme.config
  (:require [integrant.core :as ig]))

(defmethod ig/init-key
  ::config
  [_ _]
  {::port 8000
   ::counter 3
   ::db {::name "creme"
         ::username "creme"
         ::socket-address "/var/run/postgresql/.s.PGSQL.5432"}})
