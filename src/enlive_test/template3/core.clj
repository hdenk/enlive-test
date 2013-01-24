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
    (fn [params]
      [(e/at layout 
             [:title] (e/substitute (e/select nodes [:title]))
             [:notice] (e/substitute (e/html [:div#notice (params :notice)])) 
             [:content] (e/substitute (e/select nodes [:content :> :*]))
             [:header :header-content] (e/content (params :header-content))
             [:footer :footer-content] (e/content (params :footer-content)))
       params])))

(defn page-t [nodes]
  (fn [params]
    [(e/at nodes 
           [:name] (e/substitute (params :name)))
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
      <header><header-content>page header</header-content></header>
      <div id="notice">take care !</div>
      <main>
           <div>Hello World</div>
      </main>
      <footer><footer-content>page footer</footer-content></footer>
   </body>
  </html>
)
