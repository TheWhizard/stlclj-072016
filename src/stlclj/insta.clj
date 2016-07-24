(ns stlclj.insta)

(require '[clojure.pprint :as ppr])
(require '[instaparse.core :as ip])

(def record-parser0 (ip/parser (slurp "resources/record0.bnf")))

(record-parser0 (slurp "resources/simple.txt")) 
(ppr/pprint (record-parser0 (slurp "resources/simple.txt"))) 
(ip/visualize (record-parser0 (slurp "resources/simple.txt"))) 

(def record-parser1 (ip/parser (slurp "resources/record1.bnf")))

(record-parser1 (slurp "resources/simple.txt")) 
(ppr/pprint (record-parser1 (slurp "resources/simple.txt"))) 

(ip/visualize (record-parser1 (slurp "resources/simple.txt"))) 
(ip/visualize (record-parser1 (slurp "resources/simple.txt")) :options {:dpi 50}) 
(ip/visualize (record-parser1 (slurp "resources/simple.txt")) :output-file "c:/clojure/stlclj/simple.png" :options {:dpi 50}) 
