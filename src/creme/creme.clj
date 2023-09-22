(ns creme.creme
  (:gen-class)
  (:import (com.zaxxer.hikari HikariDataSource))
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args])

(comment
  (let [dbname "creme"
        username "creme"
        dbspec
        {:jdbcUrl (str "jdbc:postgresql:" dbname ; "?user=" username
                       "?socketFactory=org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg"
                       "&socketFactoryArg=/var/run/postgresql/.s.PGSQL.5432")
         :dataSourceProperties {:socketTimeout 30}}
        pool (connection/->pool com.zaxxer.hikari.HikariDataSource dbspec)]

    (with-open [ds pool]
      (.close (jdbc/get-connection ds))
      (jdbc/execute! ds ["insert into blah (val) VALUES ('coucou1')"])
      (jdbc/execute! ds ["select count(*) from blah"])
      #_(jdbc/execute! ds ["select * from blah"])))
  :nil)
