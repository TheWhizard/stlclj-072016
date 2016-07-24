(ns stlclj.simple)

(require '[clojure.pprint :as ppr])

;; edn
(require '[clojure.edn :as edn])

(def edn-string (slurp "resources/seasons.edn"))

(println edn-string)
(class edn-string)

(edn/read-string edn-string)
(class (edn/read-string edn-string))
(ppr/pprint (edn/read-string edn-string))

;; json
(require '[clojure.data.json :as json])

(def json-string (slurp "resources/seasons.json"))

(println json-string)
(class json-string)

(json/read-str json-string)
(class (json/read-str json-string))
(ppr/pprint (json/read-str json-string))

;; xml
(require '[clojure.data.xml :as xml])

(def xml-string (slurp "resources/seasons.xml"))

(println xml-string)
(class xml-string)

(xml/parse-str xml-string)
(class (xml/parse-str xml-string))
(ppr/pprint (xml/parse-str xml-string))

;; csv
(require '[clojure.data.csv :as csv])

(def csv-string (slurp "resources/seasons.csv"))

(println csv-string)
(class csv-string)

(csv/read-csv csv-string)
(class (csv/read-csv csv-string))
(ppr/pprint (csv/read-csv csv-string))

;; end
