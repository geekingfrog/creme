(ns creme.org.views
  (:require [creme.common.forms :as form]
            [creme.common.view :as view]))

(def create-org-form
  {:id "create-org"
   :method :post
   :url-path "/org"
   :fields [(form/text-field {:placeholder "name"
                              :name "org-name"
                              :display-name "Organisation name"
                              :required? true})
            (form/text-field {:name "org-slug"
                              :placeholder "slug"
                              :display-name "Url name (slug)"
                              :required? true})
            (form/switch-field {:name "visible?"
                                :display-name "Visible?"
                                :default-value false})]
   :submit-text "Create"})

(defn render-create
  [params]
  (let [form-data ((keyword (:id create-org-form)) params)
        populated-form (if form-data (form/validate-form create-org-form form-data) create-org-form)]
    ; (println "populated form: " populated-form)
    (view/base
     {:body [:main.container
             [:h1 "Create an organisation"]
             (form/render-form populated-form)]})))
