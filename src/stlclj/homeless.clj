(ns stlclj.homeless)

(require '[clojure.pprint :as ppr])

(re-matches #"^abc(.*)$" "abcxyz")

(re-matches #"^##\s*(.*)$" "## this is a comment")
(re-matches #"^##\s*(.*)$" "# this is not a comment")

(re-matches #"^r\|\s*(.+)$" "r|CX")

(require '[stlclj.hcp :as hcp])

(hcp/tokenize-comment "## this is a comment")
(hcp/tokenize-comment "# this is not a comment")

(hcp/tokenize-blank "   ")
(hcp/tokenize-blank "ss   ")

(hcp/tokenize-record "r|CX")
(hcp/tokenize-record "r      |   CX")

(hcp/tokenize-block "b|ID")
(hcp/tokenize-value "v|name")
(hcp/tokenize-iterator "I|ADDR")
(hcp/tokenize-value "x|name")

(hcp/tokenize-string "b|ID")
(hcp/tokenize-string "v|ID")
(hcp/tokenize-string "r|CX")

(hcp/tokenize-text hcp/sample-string)      
(ppr/pprint (hcp/tokenize-text hcp/sample-string))      

(->>
 hcp/sample-string
 (hcp/tokenize-text)
 (hcp/parse-root []))

(ppr/pprint (->>
             hcp/sample-string
             (hcp/tokenize-text)
             (hcp/parse-root [])))
