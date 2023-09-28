(ns creme.creme
  (:gen-class)
  (:import (com.zaxxer.hikari HikariDataSource))
  (:require
   [creme.system]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection]
   [honey.sql :as sql]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (creme.system/start)
  (println "finishing"))

(comment
  (set! *warn-on-reflection* true)
  (let [dbname "creme"
        username "creme"
        dbspec
        {:jdbcUrl (str "jdbc:postgresql:" dbname ; "?user=" username
                       "?user=" username
                       "&socketFactory=org.newsclub.net.unix.AFUNIXSocketFactory$FactoryArg"
                       "&socketFactoryArg=/var/run/postgresql/.s.PGSQL.5432")
         :dataSourceProperties {:socketTimeout 30}}
        pool (connection/->pool com.zaxxer.hikari.HikariDataSource dbspec)]

    (with-open [^HikariDataSource ds pool]
      (.close (jdbc/get-connection ds))
      ; (jdbc/execute! ds (sql/format {:delete-from :blah}))
      (let [c (-> (jdbc/execute! ds (sql/format {:select :%count.* :from [:blah]}))
                  first
                  :count)]
        (jdbc/execute! ds (sql/format {:insert-into :blah
                                       :columns [:val]
                                       :values [[(format "coucou%03d" (inc c))]]}) #_["insert into blah (val) VALUES ('coucou1')"]))
      (let [all-blah (jdbc/execute! ds (sql/format {:select [:*] :from [:blah]}))]
        all-blah)

      #_(jdbc/execute! ds ["select * from blah"])))
  :nil)
