(ns enlive-test.template3.core
  (:require [net.cgrand.enlive-html :as e]
            [clojure.algo.monads :as m]))

(defn layout-name [nodes]
  (-> nodes 
      (e/select [[:meta (e/attr= :name "layout")]])
      first :attrs :content))

(defn layout-t [nodes]
  (let [layout-name (layout-name nodes)
        layout (e/html-resource (str "enlive_test/template3/" layout-name ".html"))]
    (fn [{:keys [notice header-content footer-content] :as params}]
      [(e/at layout 
             [:title] (e/substitute (e/select nodes [:title]))
             [:div#notice] (e/substitute (e/html [:div#notice notice])) 
             [:div#main] (e/content (e/select nodes [:div#main :> :*]))
             [:div#header] (e/content header-content)
             [:div#footer] (e/content footer-content))
       params])))

(defn page-t [nodes]
  (fn [{:keys [page name] :as params}]
    [(e/at nodes 
           [:_name_] (e/substitute name))
     (-> params ; it's even possible to manipulate params to send info down the chain
       (conj [:header-content "page header"]) ; header-content could be loaded from template 
       (conj [:footer-content "page footer"]))])) ; footer-content could be load from template 

(def page 
  (m/with-monad m/state-m 
    (let [nodes (e/html-resource "enlive_test/template3/page.html") 
          transformer-fn (m/m-chain [page-t layout-t])]
      (transformer-fn nodes))))
      
(print (apply str (e/emit* (first (page {:notice "take care !" :name "World"})))))

(comment
  
; Output:
  
<html>
  <head>
    <title>Page Title</title>
  </head>
  <body>
    <div class="column" id="header">Page Header</div>
    <div id="notice">take care !</div> 
    <div class="column" id="main"><div>Hello World</div></div>
    <div class="column" id="footer">Page Footer</div>
  </body>
</html>
)
