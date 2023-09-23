(ns creme.migrations
  (:require
   [clojure.tools.cli :as cli]
   [creme.config :as config]
   [integrant.core :as ig]
   [migratus.core :as migratus]
   [creme.db :as db]
   [creme.system :as system]
   [next.jdbc :as jdbc])
  (:gen-class))

(def migration-config
  (assoc
   system/config
   ::migration-config {:config (ig/ref ::config/config)
                       :pool (ig/ref ::db/db-pool)}))

(defmethod ig/init-key
  ::migration-config
  [_ deps]
  {:store :database
   :migration-dir "migrations/"
   :migration-table-name "_migrations"
   :db {:connection (-> deps :pool jdbc/get-connection)}})

(defmethod ig/halt-key!
  ::migration-config
  [_ conf]
  (println "halting key" conf)
  (.close (-> conf :db :connection)))

(def cli-options
  [["-h" "--help"]])

(defn -main [& args]
  (println "running migration from cli with args:" args)
  (println "parsed opts" (cli/parse-opts args cli-options))
  (println "migration config: " migration-config)
  (set! *warn-on-reflection* true)
  (let [parsed (cli/parse-opts args cli-options)
        arguments (:arguments parsed)
        conf (ig/init migration-config [::migration-config])]
    (println "migration config" conf)
    (try
      (when-let [err (:errors parsed)]
        (throw (ex-info "Invalid arguments" {:errors err})))
      (cond
        (empty? arguments) (migratus/migrate conf)

        (= "create" (first arguments))
        (migratus/create conf (second arguments))

        (= "rollback" (first arguments))
        (migratus/rollback conf)

        (= "reset" (first arguments))
        (migratus/reset conf)

        :else (throw (RuntimeException. (str "unknown arguments " arguments))))
      (finally
        (ig/halt! conf)))))

(comment
  (ig/init migration-config [::migration])
  (migratus/create migration-config "testing-migration")
  (migratus/rollback migration-config)
  (migratus/migrate migration-config))
