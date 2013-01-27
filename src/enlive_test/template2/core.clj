(ns enlive-test.template2.core
  (:use [net.cgrand.enlive-html
         :only [deftemplate defsnippet content clone-for 
                nth-of-type first-child do-> set-attr sniptest at emit*]]))

(def dummy-context
     {:title "Enlive Template2 Example"
      :sections [{:title "Clojure"
                  :data [{:text "Macros"
                          :href "http://www.clojure.org/macros"}
                         {:text "Multimethods & Hierarchies"
                          :href "http://www.clojure.org/multimethods"}]}
                 {:title "Compojure"
                  :data [{:text "Requests"
                          :href "http://www.compojure.org/docs/requests"}
                         {:text "Middleware"
                          :href "http://www.compojure.org/docs/middleware"}]}
                 {:title"Clojars"
                  :data [{:text "Clutch"
                          :href "http://clojars.org/org.clojars.ato/clutch"}
                         {:text "JOGL2"
                          :href "http://clojars.org/jogl2"}]}
                 {:title "Enlive"
                  :data [{:text "Getting Started"
                          :href "http://wiki.github.com/cgrand/enlive/getting-started"}
                         {:text "Syntax"
                          :href "http://enlive.cgrand.net/syntax.html"}]}]})

; we only want to select a model link
(def link-sel [[:.content (nth-of-type 1)] :> first-child])

(defsnippet link-model "enlive_test/template2/example.html" link-sel
  [{:keys [text href]}]
  [:a] (do->
        (content text)
        (set-attr :href href)))

; we only want to select the model h2 ul range
(def section-sel {[:.title] [[:.content (nth-of-type 1)]]})

(defsnippet section-model "enlive_test/template2/example.html" section-sel
  [{:keys [title data]} model]
  [:.title]   (content title)
  [:.content] (content (map model data)))

(deftemplate index "enlive_test/template2/example.html"
  [{:keys [title sections]} model]
  [:#title] (content title)
  [:body]   (content (map #(model % link-model) sections))) ; section-model takes 2 params

; TODO find out: resulting HTML is escaped. but why ?
(print (apply str (emit* (index dummy-context section-model))))

