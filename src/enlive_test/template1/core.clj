(ns enlive-test.template1.core
  (:require [net.cgrand.enlive-html :as e]))

(defmacro maybe-content
  ([expr] `(if-let [x# ~expr] (e/content x#) identity))
  ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(defn layout
  [{:keys [title notice main header footer]}]
  (let [layout (e/html-resource "enlive_test/template1/layout.html")]
		(e/at layout
		  [:title] (maybe-content title)
		  [:div#notice] (maybe-content notice) 
		  [:div#main] (e/content main)
		  [:div#header] (maybe-content header)
		  [:div#footer] (maybe-content footer))))

(defn page-t 
  [{:keys [page name]}]
  (e/at page 
    [:_name_] (e/substitute name)))

(defn page [params]
  (let [page-r (e/html-resource "enlive_test/template1/page.html")
        page (page-t  {:page page-r})]                                                               
    (layout {:title (e/select page [:title :> e/text-node])  
             :notice (params :notice)
             :main (e/select page [:div#main :> :*])
             :header "Page Header" 
             :footer nil})))

(print (apply str (e/emit* (page {:notice "take care !" :name "World"}))))

(comment

  ; Output:
  <html>
  <head>
    <title>Page Title</title>
  </head>
  <body>
    <div class="column" id="header">Page Header</div>
    <div id="main">
      <div>Hello World</div>
    </div>
    <div class="column" id="footer">Page Footer</div>
  </body>
  </html>
)
