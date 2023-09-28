(ns user
  (:require [creme.system :as system]
            [integrant.repl :as igr]
            [hiccup-bridge.core :as hicv]))

(igr/set-prep! (constantly system/config))

(comment
  (igr/go)
  (igr/halt)
  (igr/reset))

(comment
  (hicv/html->hiccup "<foo required><bar>buzz</bar></foo>")
  (hicv/hiccup->html [:foo {:required "true"} [:bar "buzz"]]))
