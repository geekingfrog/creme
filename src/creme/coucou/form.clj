(ns creme.coucou.form
  (:require [hiccup2.core :as h]
            [hiccup.page :refer [html5]]
            [hiccup.form :as hf]
            [hiccup.util :as util]
            [taoensso.timbre :as timbre]))

(defprotocol FormField
  (deserialize [field raw-data])
  (serialize [field])
  (validate [field data])
  (render [field]))

(defrecord Field [field-map]
  FormField
  (deserialize [{{:keys [deserialize-fn]
                  :or {deserialize-fn identity}} :field-map} raw-data]
    (deserialize-fn raw-data))
  (serialize [{{:keys [serialize-fn value]
                :or {serialize-fn identity}} :field-map}]
    (serialize-fn value))
  (validate [{:keys [field-map]} data]
    (if-let [validate-fn (:validate-fn field-map)]
      (validate-fn data)
      {:ok data}))
  (render [{:keys [field-map] :as this}]
    ((:render-fn field-map) this)))

(defn input-field-foo
  ([opts hiccup-fn] (input-field-foo opts hiccup-fn {}))
  ([{:keys [placeholder name display-name id] :as opts} hiccup-fn input-field-props]
   (let [placeholder (if (some? placeholder) {:placeholder placeholder} {})
         field-name (or id name)
         render-fn
         (fn render-text-field [this]
           (let [err (-> this :field-map :errors)
                 props (if err {:aria-invalid "true"} {})]
             [:fieldset
              (hf/label field-name (or display-name name))
              (when err [:span.error (util/as-str err)])
              (hiccup-fn (merge props input-field-props) field-name (serialize this))]))]
     (->Field (merge {:placeholder placeholder
                      :render-fn render-fn} opts)))))

(defn text-field [{:keys [placeholder name display-name id] :as opts}]
  (let [field-props (if (some? placeholder) {:placeholder placeholder} {})
        field-name (or id name)
        render-fn
        (fn render-text-field [this]
          (let [err (-> this :field-map :errors)
                props (if err {:aria-invalid "true"} {})]
            [:fieldset
             (hf/label field-name (or display-name name))
             (when err [:span.error (util/as-str err)])
             (hf/text-field (merge field-props props) field-name (serialize this))]))]
    (->Field (merge {:placeholder placeholder
                     :render-fn render-fn} opts))))

(defn switch-field [{:keys [name display-name id] :as opts}]
  (let [field-name (or id name)
        validate-fn
        (fn validate-bool-field
          [this]
          {:ok (case (-> this :field-map :raw-data)
                 ("" nil "false") false
                 "true" true
                 (or (-> this :field-map :default-value) false))})

        render-fn
        (fn render-switch-field [this]
          (let [checked? (or (-> this :field-map :value) (-> this :field-map :default-value) false)]
            [:fieldset
             (hf/label field-name (or display-name name))
             (hf/check-box {:checked checked? :role "switch"}
                           field-name (serialize this))]))]
    (->Field (merge {:render-fn render-fn :validate-fn validate-fn} opts))))

(defn render-form [{:keys [id fields method url-path submit-text]}]
  (hf/with-group id
    (hf/form-to
     {:id id}
     [method url-path]
     (concat (mapv render fields))
     (hf/submit-button (or submit-text "Submit")))))

;; copied from hiccup.form
(defn make-id
  "Create a field id from the supplied argument and current field group."
  [name]
  (reduce #(str %1 "-" %2)
          (conj hf/*group* (util/as-str name))))

(defn make-name
  "Create a field name from the supplied argument the current field group."
  [name]
  (reduce #(str %1 "[" %2 "]")
          (conj hf/*group* (util/as-str name))))

(defn input-field
  "Creates a new <input> element."
  [type name value]
  [:input {:type  type
           :name  (make-name name)
           :id    (make-id name)
           :value value}])

(defn render-field [field]
  [:fieldset
   ; (label (:name field))
   ])

(defn field [field-map]
  (merge
   {:validate-fn identity
    :deserialize-fn identity
    :serialize-fn identity
    :render-fn render-field}
   field-map))

; (comment
;   (field {}))

(defn validate-form
  "Populate all the field with the given data, running their deserializers and validation
  functions. Also run the form's validation function (if it exists)"
  [form form-data]
  (update form :fields
          #(mapv (fn validate-field [field]
                   (let [raw-data (get form-data (-> field :field-map :name keyword))
                         {:keys [ok err] :as x} (validate field raw-data)
                         value (if (nil? err) ok raw-data)]
                     (-> field
                         (assoc-in [:field-map :raw-data] raw-data)
                         (assoc-in [:field-map :value] value)
                         (assoc-in [:field-map :errors] err))))
                 %))
  ; TODO run the form overall validation on that
  )

(defn render-page-form
  [form params]
  (let [form-data ((keyword (:id form)) params)
        populated-form (validate-form form form-data)]
    (html5
     (h/html
      [:head
       [:link {:rel "stylesheet" :href "/staticfiles/pico.min.css"}]
       [:link {:rel "stylesheet" :href "/staticfiles/styles.css"}]]
      [:body
       [:main.container
        [:h1 "coucou form experiment"]
        (render-form populated-form)]]))))

(defn render-page-kitchen [_params]
  (html5
   (h/html
    [:head
     [:link {:rel "stylesheet" :href "/staticfiles/pico.min.css"}]
     [:link {:rel "stylesheet" :href "/staticfiles/styles.css"}]]
    [:body
     [:main.container
      [:h1 "coucou form experiment here"]
      (hf/with-group
        "form1"
        (hf/form-to
         [:post "/formtest"]
         [:fieldset
          (hf/label "org-name" "Organisation name")
          (hf/text-field {:placeholder "placeholder org name"} "org-name" "")]
         ; (hf/hidden-field "coucou" "ook")
         (hf/submit-button "submit here")))

      [:hr]
      (hf/with-group
        "form2"
        (hf/form-to
         [:post "formtest"]

         [:fieldset
          (hf/label "visible?" "Visible?")
          (hf/check-box "visible?")]

         [:fieldset
          (hf/label "notify-me" "notify me!")
          (hf/check-box {:role "switch"} "notify-me" true)]

         [:fieldset
          (hf/label "harass-me" "Harass me!")
          (hf/check-box {:role "switch"} "harass-me" false)]

         [:fieldset
          (hf/label "when?" "When?")
          (input-field "date" "when?" "2023-09-23")]

         (hf/label "category" "Choose a category")
         (hf/drop-down "category" ["gold" "silver" "bronze"] "bronze")
         (hf/submit-button "submit second form here")))]])))

(defn render-page
  ([] (render-page {}))
  ([params] (render-page-form
             {:id "omiform"
              :method :post
              :url-path "/formtest"
              :fields [(text-field {:placeholder "write things here !"
                                    :name "org-name"
                                    :display-name "Organisation name"})
                       (text-field {:placeholder "write other things here !"
                                    :name "other-org"
                                    :display-name "Other org"})
                       (switch-field {:name "visible?"
                                      :display-name "Visible?"
                                      :default-value false})]
              :submit-text "Save"}
             params)))
