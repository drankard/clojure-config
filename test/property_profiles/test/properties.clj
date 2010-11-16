(ns property-profiles.test.properties
  (:import (java.net InetAddress))
  (:require [property-profiles.properties :as p])
  (:use [clojure.test]))

(def profiles [{:name "m14758" :type "user" :parent "ci"}		 
	    {:name "ci" :type "host"}
	    {:name "XPN55422" :type "host"}])


(deftest test-host
  (let [host (. (. InetAddress getLocalHost) getHostName)
	param  {:name host :type "host"}]
    
    (is host (#'p/hostname))
    (is (#'p/host-match? param))))


  

