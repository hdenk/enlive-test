(ns enlive-test.site1.core
  (:require [net.cgrand.enlive-html :as html]))

(defmacro maybe-substitute
  ([expr] `(if-let [x# ~expr] (html/substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

;; 
;; Templates 
;;

(html/deftemplate base "enlive_test/site1/base.html"
  [{:keys [title header main footer]}]
  [:#title]  (maybe-content title)
  [:#header] (maybe-substitute header)
  [:#main]   (maybe-substitute main)
  [:#footer] (maybe-substitute footer))

(html/defsnippet three-col "enlive_test/site1/3col.html" [:div#main]
  [{:keys [left middle right]}]
  [:div#left]   (maybe-substitute left)
  [:div#middle] (maybe-substitute middle)
  [:div#right]  (maybe-substitute right))

(html/defsnippet nav1 "enlive_test/site1/navs.html" [:div#nav1] [])
(html/defsnippet nav2 "enlive_test/site1/navs.html" [:div#nav2] [])
(html/defsnippet nav3 "enlive_test/site1/navs.html" [:div#nav3] [])

;; 
;; Pages
;; 

(defn viewa []
  (base {:title "View A"
         :main (three-col {})}))

(defn viewb []
  (let [navl (nav1)
        navr (nav2)]
   (base {:title "View B"
          :main (three-col {:left  navl
                            :right navr})})))

(defn viewc
  ([] (viewc nil))
  ([action]
     (let [navs [(nav1) (nav2)]
           [navl navr] (if (= action "reverse") (reverse navs) navs)]
       (base {:title "View C"
              :main (three-col {:left  navl
                                :right navr})}))))

(defn index
  ([] (base {}))
  ([ctxt] (base ctxt)))

;(println (apply str (html/emit* (viewc))))
(println (apply str (html/emit* (viewc "reverse"))))
