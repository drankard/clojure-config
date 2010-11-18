(ns property-profiles.test.properties
  (:import (java.net InetAddress))
  (:require [property-profiles.properties :as p])
  (:use [clojure.test]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))


(defn hostname []
  (. (. InetAddress getLocalHost) getHostName))

(defn prepare-params-with-username []
   (let [user (System/getProperty "user.name")]
     (p/set-profiles [{:name "default" :type "user" :value user}])))
	
(defn prepare-params-with-hostname []
  (p/set-profiles [{:name "foo" :type "host" :value (hostname)}]))


(describe user-match?
	 (given [user (System/getProperty "user.name")
		 param {:name "foo" :type "user" :value user}]		
		(it "test if the username matches"
		    (#'p/user-match? param))))

(describe host-match?
	  (given [host (. (. InetAddress getLocalHost) getHostName)
		  param  {:type "host" :value host}]
		 (it "hostname should match local hostname"
		     (= host (#'p/hostname)))
		 (it "hostname should match given params"		     
		     (= (#'p/host-match? param)))))

(describe get-property-file
	  (given [param {:name "foo" :type "user" :value "some-user"}]
		 (it "property file should match given username"
		     (= "foo.properties" (#'p/get-property-file param)))))



(describe get-filter-by-rule
	  (with [(before (prepare-params-with-username))]
		(it "it should match with username params"
		    (= (System/getProperty "user.name") (:value (#'p/filter-by-rule)))))
	  (with [(before (prepare-params-with-hostname))]
		(it "it should match with hostname params"
		    (= (. (. InetAddress getLocalHost) getHostName)(:value (#'p/filter-by-rule))))))

