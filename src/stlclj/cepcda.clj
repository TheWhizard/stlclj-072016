(ns stlclj.cepcda)

(require '[instaparse.core :as insta])
(require '[clojure.string :as str])
(require '[clojure.data.json :as json])
;; better support for pretty printing
(require '[cheshire.core :as chesh])
(require '[clojure.pprint :as ppr])

;; would it be better for these local funtions to be extracted?
;; easier to test individually...

(defn convert-cda-ast [[cda record blocks comments & extra]]
  (defn get-comments [[kw & comments]]
    (do
      (assert (= :comments kw))
      (assert (nil? extra))
      (if comments (vec comments) [])))
  (defn get-meta [assert-kw [kw [_ meta-data]]]
    (do
      (assert (= assert-kw kw))
      (if meta-data (json/read-str meta-data) {})))
  (defn get-second [assert-kw [kw x-second]]
    (do
      (assert (= assert-kw kw))
      x-second))
  (defn get-remarks [[kw & remarks]]
    (do
      (assert (= :remarks kw))
      (if remarks (vec remarks) [])))
  (defn convert-record [[record comments record-meta x-name root-block remarks & extra]]
    (do
      (assert (= :record record))
      (assert (nil? extra))
      {:comments (get-comments comments) 
       :meta (get-meta :record-meta record-meta) 
       :name (get-second :name x-name) 
       :root-block (get-second :root-block root-block)
       :remarks (get-remarks remarks)}))
  (defn convert-value [[kw comments value-meta x-name x-type sub-type remarks & extra]]
    (defn get-type [[kw [x-type _]]]
      (do
        (assert (= :type kw))
        x-type))
    (defn get-sub-type [[kw [x-type _]]]
      (do
        (assert (= :sub-type kw))
        x-type))
    (do
      (assert (= :value kw))
      (assert (nil? extra))
      {:value {:comments (get-comments comments)
               :meta (get-meta :value-meta value-meta)
               :name (get-second :name x-name)
               :type (get-type x-type)
               :sub-type (get-sub-type sub-type)
               :remarks (get-remarks remarks)}}))
  (defn convert-values [[kw & values]]
    (do
      (assert (= :values kw))
      (assert (seq? values))
      (->>
       values
       (map convert-value)
       (vec))))
  (defn convert-iterator [[kw comments iterator-meta x-name block-name remarks & extra]]
    (do
      (assert (= :iterator kw))
      (assert (nil? extra))
      {:iterator {:comments (get-comments comments)
                  :meta (get-meta :iterator-meta iterator-meta)
                  :name (get-second :name x-name)
                  :block-name (get-second :block-name block-name)
                  :remarks (get-remarks remarks)}}))
  (defn convert-iterators [[kw & iterators]]
    (do
      (assert (= :iterators kw))
      (->>
       iterators
       (map convert-iterator)
       (vec))))
  (defn convert-block [[kw comments block-meta x-name remarks values iterators]]
    (do
      (assert (= :block kw))
      {:block {:comments (get-comments comments)
               :meta (get-meta :block-meta block-meta)
               :name (get-second :name x-name)
               :remarks (get-remarks remarks)
               :values (convert-values values)
               :iterators (convert-iterators iterators)}}))
  (defn convert-blocks [[kw & blocks]]
    (do
      (assert (= :blocks kw))
      (assert (seq? blocks))
      (->>
       blocks
       (map convert-block)
       (vec))))
  (do
    (assert (= :CDA cda))
    {:CDA {:record (convert-record record) 
           :blocks (convert-blocks blocks) 
           :comments (get-comments comments)}}))

(defn get-blocks-to-code-generate [mapped-cda]
  (->>
   (get-in mapped-cda [:CDA :blocks])
   (filter #(get-in % [:block :meta "code-generate"]))
   (vec)))

(defn get-block-special-values [block special-value]
  (->>
   (get-in block [:block :values])
   (filter #(get-in % [:value :meta special-value]))
   (map #(get-in % [:value :name]))
   (vec)))

(defn make-comma-separated-string [sl]
  (->>
   sl
   (map #(str "\"" % "\""))
   (interpose ", ")
   (apply str)))

(defn generate-root-combine [block]
  (let [block-key (get-in block [:block :name])
        code-generate (get-in block [:block :meta "code-generate"])
        combine-generator (get-in block [:block :meta "combine-generator"])
        filter-fields (get-block-special-values block "filter-field")
        key-fields (get-block-special-values block "key-field")
        combine-fields (get-block-special-values block "combine-field")
        template-map {"${BLOCK_KEY}" block-key
                      "${COMMA_SEPARATED_FILTER_FIELDS}" (->> filter-fields (make-comma-separated-string))
                      "${FILTER_FIELDS_COUNT}" (str (count filter-fields))
                      "${COMMA_SEPARATED_KEY_FIELDS}" (->> key-fields (make-comma-separated-string))
                      "${KEY_FIELDS_COUNT}" (str (count key-fields))
                      "${COMMA_SEPARATED_COMBINE_FIELDS}" (->> combine-fields (make-comma-separated-string))
                      "${COMBINE_FIELDS_COUNT}" (str (count combine-fields))}]
    (do
      (assert code-generate)
      (assert (= "root-combine" combine-generator))
      template-map)))

(defn generate-child-combine [block]
  (let [block-key (get-in block [:block :name])
        code-generate (get-in block [:block :meta "code-generate"])
        combine-generator (get-in block [:block :meta "combine-generator"])
        filter-fields (get-block-special-values block "filter-field")
        key-fields (get-block-special-values block "key-field")
        history-fields (get-block-special-values block "history-field")
        combine-fields (get-block-special-values block "combine-field")
        nochangeupdate-fields (get-block-special-values block "nochangeupdate-field")
        nohistorymerge-fields (get-block-special-values block "nohistorymerge-field")
        datecheck-fields (get-block-special-values block "datecheck-field")
        template-map {"${BLOCK_KEY}" block-key
                      "${PARENT_BLOCK_KEY}" "CX"
                      "${COMMA_SEPARATED_FILTER_FIELDS}" (->> filter-fields (make-comma-separated-string))
                      "${FILTER_FIELDS_COUNT}" (str (count filter-fields))
                      "${COMMA_SEPARATED_KEY_FIELDS}" (->> key-fields (make-comma-separated-string))
                      "${KEY_FIELDS_COUNT}" (str (count key-fields))
                      "${COMMA_SEPARATED_HISTORY_FIELDS}" (->> history-fields (make-comma-separated-string))
                      "${HISTORY_FIELDS_COUNT}" (str (count history-fields))
                      "${COMMA_SEPARATED_COMBINE_FIELDS}" (->> combine-fields (make-comma-separated-string))
                      "${COMBINE_FIELDS_COUNT}" (str (count combine-fields))
                      "${COMMA_SEPARATED_NO_CHANGE_UPDATE_FIELDS}" (->> nochangeupdate-fields (make-comma-separated-string))
                      "${NO_CHANGE_UPDATE_FIELDS_COUNT}" (str (count nochangeupdate-fields))
                      "${COMMA_SEPARATED_NO_HISTORY_MERGE_FIELDS}" (->> nohistorymerge-fields (make-comma-separated-string))
                      "${NO_HISTORY_MERGE_FIELDS_COUNT}" (str (count nohistorymerge-fields))
                      "${COMMA_SEPARATED_DATE_CHECK_FIELDS}" (->> datecheck-fields (make-comma-separated-string))
                      "${DATE_CHECK_FIELDS_COUNT}" (str (count datecheck-fields))}]
    (do
      (assert code-generate)
      (assert (= "child-combine" combine-generator))
      template-map)))

(defn generate-combine [block]
  (let [combine-generator (get-in block [:block :meta "combine-generator"])]
    (case combine-generator
      "root-combine" (generate-root-combine block)
      "child-combine" (generate-child-combine block))))

(defn apply-template! [input-template-filename output-base-filename output-directoryname template-map]
  (let [template-text (slurp input-template-filename)
        output-filename (str output-directoryname "/" output-base-filename)]
    (do
      (->>
       (keys template-map)
       (reduce #(str/replace %1 %2 (template-map %2)) template-text)
       (spit output-filename))
      template-map)))

(defn generate-code2! [block output-directory]
  (let [combine-generator (get-in block [:block :meta "combine-generator"])
        block-name (get-in block [:block :name])
        template-filenames {"root-combine" {:h "resources/RootCombineBlock.h.template"
                                            :cpp "resources/RootCombineBlock.cpp.template"}
                            "child-combine" {:h "resources/ChildCombineBlock.h.template"
                                             :cpp "resources/ChildCombineBlock.cpp.template"}}]
    (do
      (->>
       (generate-combine block)
       (apply-template! (get-in template-filenames [combine-generator :h]) (str block-name "Block.h") output-directory)
       (apply-template! (get-in template-filenames [combine-generator :cpp]) (str block-name "Block.cpp") output-directory)))))

; note that the BNF forces certain assumptions about the structure
; this may or may not be the exact parsing done by pxhformat.cpp and pxcsv.cpp
(def cda-grammar-filename "resources/cepcda.bnf")
; generate the parser
(def cda-parser (insta/parser (slurp cda-grammar-filename)))

; parse the annotated CDA
(cda-parser (slurp "resources/annotated.cepcda"))

;; dump the data structures

(let [cda-grammar-filename cda-grammar-filename 
      cda-parser (insta/parser (slurp cda-grammar-filename))
      input-filename "resources/annotated.cepcda"
      parsed-cda (cda-parser (slurp input-filename))
      mapped-cda (convert-cda-ast parsed-cda)]
  (do
    (spit "c:/clojure/stlclj/__generated_code__/parsed-cda.txt" (with-out-str (ppr/pprint parsed-cda)))
    (spit "c:/clojure/stlclj/__generated_code__/mapped-cda.txt" (with-out-str (ppr/pprint mapped-cda)))))

;; codegen here

;; raw cda, 0 blocks will be processed
(let [cda-grammar-filename cda-grammar-filename 
      cda-parser (insta/parser (slurp cda-grammar-filename))
      input-filename "resources/raw.cepcda"
      parsed-cda (cda-parser (slurp input-filename))
      mapped-cda (convert-cda-ast parsed-cda)]
  (->>
   mapped-cda
   (get-blocks-to-code-generate)
   (map #(generate-code2! % "c:/clojure/stlclj/__generated_code__"))
   (count)))

;; annotated cda, 3 blocks will be processed
(let [cda-grammar-filename cda-grammar-filename 
      cda-parser (insta/parser (slurp cda-grammar-filename))
      input-filename "resources/annotated.cepcda"
      parsed-cda (cda-parser (slurp input-filename))
      mapped-cda (convert-cda-ast parsed-cda)]
  (->>
   mapped-cda
   (get-blocks-to-code-generate)
   (map #(generate-code2! % "c:/clojure/stlclj/__generated_code__"))
   (count)))

;; generate JSON version of the CDA
(let [cda-grammar-filename cda-grammar-filename 
      cda-parser (insta/parser (slurp cda-grammar-filename))
      input-filename "resources/annotated.cepcda"
      parsed-cda (cda-parser (slurp input-filename))
      mapped-cda (convert-cda-ast parsed-cda)]
  (spit "c:/clojure/stlclj/__generated_code__/canada-cda-annotated.json" (chesh/generate-string mapped-cda {:pretty true})))
