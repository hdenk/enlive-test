(ns enlive-test.core-tests
  (:use [clojure.test :only [deftest is]])
  (:require [enlive-test.template1.core :as template1])
  (:require [enlive-test.template2.core :as template2])
  (:require [enlive-test.template3.core :as template3])
  (:require [enlive-test.scrape1.core :as scrape1])
  (:require [enlive-test.site1.core :as site1]))

; in diesem Fall ist das reine Laden der Namespaces schon ein halbwegs guter 
; Test da dadurch die Transformations-Logik compiliert und ausgeführt wird. 

 ; die Eins muß stehen ... zur Beruhigung ;-)
(deftest test-that-passes []
  (is true))