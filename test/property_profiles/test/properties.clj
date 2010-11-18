(ns property-profiles.test.properties
  (:import (java.net InetAddress))
  (:require [property-profiles.properties :as p])
  (:use [clojure.test]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))


(p/set-profiles [{:name "m14758" :type "user" :parent "ci"}		 
	    {:name "ci" :type "host"}
	    {:name "XPN55422" :type "host"}])




(describe user-match?
	 (given [user (System/getProperty "user.name")
		 param {:name user :type "user"}]		
		(it "test if the username matches"
		    (#'p/user-match? param))))

(describe host-match?
	  (given [host (. (. InetAddress getLocalHost) getHostName)
		  param  {:name host :type "hosts"}]
		 (it "hostname should match local hostname"
		     (= host (#'p/hostname)))
		 (it "hostname should match given params"		     
		     (= (#'p/host-match? param)))))

(describe get-property-file
	  (given [param {:name "foo" :type "user"}]
		 (it "property file should match given username"
		     (= "foo.properties" (#'p/get-property-file param)))))
		 

(defn hostname []
  (. (. InetAddress getLocalHost) getHostName))

(defn prepare-params-with-username []
   (let [user (System/getProperty "user.name")]
     (p/set-profiles [{:name user :type "user"}])))
	
(defn prepare-params-with-hostname []
     (p/set-profiles [{:name (hostname) :type "host"}]))


(describe get-filter-by-rule
	  (with [(before (prepare-params-with-username))]
		(it "it should match with username params"
		    (= (System/getProperty "user.name") (:name (#'p/filter-by-rule)))))
	  (with [(before (prepare-params-with-hostname))]
		(it "it should match with hostname params"
		    (= (. (. InetAddress getLocalHost) getHostName)(:name (#'p/filter-by-rule))))))

