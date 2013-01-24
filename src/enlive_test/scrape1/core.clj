(ns enlive-test.scrape1.core
  (:require [net.cgrand.enlive-html :as html]))

(defn content->string [content]
  (cond
   (nil? content)    ""
   (string? content) content
   (map? content)    (content->string (:content content))
   (coll? content)   (apply str (map content->string content))
   :else             (str content)))
 
(derive clojure.lang.PersistentStructMap ::Map)
(derive clojure.lang.PersistentArrayMap  ::Map)
(derive java.lang.String                 ::String)
(derive clojure.lang.ISeq                ::Collection)
(derive clojure.lang.PersistentList      ::Collection)
(derive clojure.lang.LazySeq             ::Collection)
 
(defn tag-type [node]
  (case (:tag node) 
   :ul    ::CompoundNode
   :li    ::TerminalNode
   :h2    ::TerminalNode
   :p     ::IgnoreNode
   ::IgnoreNode))
 
(defmulti parse-node
  (fn [node]
    (let [cls (class node)] [cls (if (isa? cls ::Map) (tag-type node) nil)])))
 
(defmethod parse-node [::Map ::TerminalNode] [node]
  (content->string (:content node)))
(defmethod parse-node [::Map ::CompoundNode] [node]
  (map parse-node (:content node)))
(defmethod parse-node [::Map ::IgnoreNode] [node]
  (parse-node (:content node)))
(defmethod parse-node [::String nil] [node]
  node)
(defmethod parse-node [::Collection nil] [node]
  (map parse-node node))
 
(defn parse  []     
 (let [nodes (html/html-resource "enlive_test/scrape1/example.html")
       body-nodes (html/select nodes [:body])]
   (for [node body-nodes] (parse-node node)))) 

(print (parse))