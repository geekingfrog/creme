(ns creme.common.forms
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
  (deserialize [{{:keys [deserialize-fn]} :field-map} raw-data]
    (if deserialize-fn (deserialize-fn raw-data) {:ok raw-data}))
  (serialize [{{:keys [serialize-fn value]
                :or {serialize-fn identity}} :field-map}]
    (serialize-fn value))
  (validate [{:keys [field-map]} data]
    (if-let [validate-fn (:validate-fn field-map)]
      (validate-fn data)
      {:ok data}))
  (render [{:keys [field-map] :as this}]
    ((:render-fn field-map) this)))

(defn text-field [{:keys [placeholder name display-name id required?] :as opts}]
  (let [field-props (-> {}
                        (assoc :placeholder (or placeholder display-name name))
                        (cond-> required? (assoc :required "true")))
        field-name (or id name)
        render-fn
        (fn render-text-field [this]
          (let [err (-> this :field-map :errors)
                props (if err {:aria-invalid "true"} {})]
            [:fieldset
             (hf/label field-name (or display-name name))
             (when err [:span.error (util/as-str err)])
             (hf/text-field (merge field-props props) field-name (serialize this))]))

        validate-fn
        (fn validate-text-field [data]
          (if (and required? (or (nil? data) (empty? data)))
            {:err "Field is required"}
            {:ok data}))]
    (->Field (merge {:placeholder placeholder
                     :render-fn render-fn
                     :validate-fn validate-fn} opts))))

(defn switch-field [{:keys [name display-name id] :as opts}]
  (let [field-name (or id name)
        deserialize-fn
        (fn deserialize-bool-field
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
    (->Field (merge {:render-fn render-fn :deserialize-fn deserialize-fn} opts))))

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

(defn validate-form
  "Populate all the field with the given data, running their deserializers and validation
  functions. Also run the form's validation function (if it exists)"
  [form form-data]
  (update form :fields
          #(mapv (fn validate-field [field]
                   (let [raw-data (get form-data (-> field :field-map :name keyword))
                         {:keys [ok err]} (deserialize field raw-data)
                         {:keys [ok err]} (if (nil? err) (validate field ok) {:err err})
                         value (if (nil? err) ok raw-data)]
                     (-> field
                         (assoc-in [:field-map :raw-data] raw-data)
                         (assoc-in [:field-map :value] value)
                         (assoc-in [:field-map :errors] err))))
                 %))
  ; TODO run the form overall validation on that
  )
