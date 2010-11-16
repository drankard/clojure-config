(ns property-profiles.test.core
  (:import (java.net InetAddress))
  (:require [property-profiles.params :as p])
  (:use [clojure.test]))

(def profiles [{:name "m14758" :type "user" :parent "ci"}		 
	    {:name "ci" :type "host"}
	    {:name "XPN55422" :type "host"}])
(binding [*profiles* (profiles)]

(deftest test-host
  (let [host (. (. InetAddress getLocalHost) getHostName)
	param  {:name host :type "host"}]
    
    (is host (#'p/hostname))
    (is (#'p/host-match? param))))


  

