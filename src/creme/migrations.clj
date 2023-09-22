(ns creme.migrations
  (:require [migratus.core :as migratus]))

(def config {:store :database
             :migration-dir "migrations/"
             :migration-table-name "_migrations"
             ; :db "jdbc:postgresql://localhost/creme?user=creme"
             :db {:dbtype "postgres"
                  :dbname "creme"
                  :user "creme"}})

(comment
  (migratus/create config "testing-migration")
  (migratus/rollback config)
  (migratus/migrate config))
