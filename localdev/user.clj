(ns user
  (:require [creme.system :as system]
            [integrant.repl :as igr]))


(igr/set-prep! (constantly system/config))

(comment
  (igr/go)
  (igr/halt)
  (igr/reset))
