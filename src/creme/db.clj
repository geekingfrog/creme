(ns creme.db
  (:import (com.zaxxer.hikari HikariDataSource))
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [creme.config :as config]))

(defmethod ig/init-key
  ::db-pool
  [_ {conf :config}]
  (let [{:keys [::config/name ::config/username ::config/socket-address]} (::config/db conf)
        dbspec {:jdbcUrl (str "jdbc:postgresql:" name
                              "?user=" username
                              "&socketFactory=org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg"
                              "&socketFactoryArg=" socket-address)
                :dataSourceProperties {:socketTimeout 30}}
        ^HikariDataSource pool (connection/->pool com.zaxxer.hikari.HikariDataSource dbspec)]
    ; init the pool so that it's not initialized on the first connection
    (.close (jdbc/get-connection pool))
    pool))

(defmethod ig/halt-key!
  ::db-pool
  [_ ^HikariDataSource datasource]
  (.close datasource))
