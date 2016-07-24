(ns stlclj.hcp)

(require '[clojure.pprint :as ppr])
(require '[clojure.string :as str])

(def simple-string (slurp "resources/simple.txt"))

(println simple-string)

(defn tokenize-comment [s]
  (when-let [c (re-matches #"^##\s*(.*)$" s)]
    [:comment (c 1)]))

(defn tokenize-blank [s]
  (when-let [c (re-matches #"^\s*$" s)]
    [:blank ""]))

(defn tokenize-record [s]
  (when-let [c (re-matches #"^r\s*\|\s*(.+)$" s)]
    [:record (c 1)]))

(defn tokenize-block [s]
  (when-let [c (re-matches #"^b\s*\|\s*(.+)$" s)]
    [:block (c 1)]))

(defn tokenize-value [s]
  (when-let [c (re-matches #"^v\s*\|\s*(.+)$" s)]
    [:value (c 1)]))

(defn tokenize-iterator [s]
  (when-let [c (re-matches #"^I\s*\|\s*(.+)$" s)]
    [:iterator (c 1)]))

(defn tokenize-string [s]
  (let [tokenizers [tokenize-comment tokenize-blank tokenize-record tokenize-block tokenize-value tokenize-iterator]]
    ((apply some-fn tokenizers) s)))

(defn tokenize-text [t]
  (->>
   t
   (str/split-lines)
   (map tokenize-string)))

(declare parse-root)

(defn parse-record [acc record tokens]
  (parse-root (conj acc record) tokens))

;; a block has this structure [:block "name" [values] [iterators]]
(defn parse-block [acc block tokens]
  (let [token (first tokens)
        token-type (first token)]
    (case token-type
      :comment (parse-block acc block (rest tokens))
      :blank (parse-block acc block (rest tokens))
      :value (parse-block acc [(block 0) (block 1) (conj (block 2) token) (block 3)] (rest tokens))
      :iterator (parse-block acc [(block 0) (block 1) (block 2) (conj (block 3) token)] (rest tokens))
      (parse-root (conj acc block) tokens))))

(defn parse-root [acc tokens]
  (let [token (first tokens)
        token-type (first token)]
    (case token-type
      :comment (parse-root acc (rest tokens))
      :blank (parse-root acc (rest tokens))
      :record (parse-record acc token (rest tokens))
      :block (parse-block acc [(token 0) (token 1) [] []] (rest tokens))
      acc)))

(->>
 simple-string
 (tokenize-text)
 (parse-root []))

(ppr/pprint 
 (->>
  simple-string
  (tokenize-text)
  (parse-root [])))

(ppr/pprint 
 (->>
  "resources/simple.txt"
  (slurp)
  (tokenize-text)
  (parse-root [])))

(ppr/pprint 
 (->>
  "resources/simple.txt"
  (slurp)
  (tokenize-text)
  (parse-root [])
  (group-by #(nth % 0))))
