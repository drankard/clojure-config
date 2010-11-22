(ns property-profiles.test.properties
  (:import (java.net InetAddress))
  (:require [property-profiles.properties :as p])
  (:use [lazytest.context.stub]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))


(defn prepare-params-with-username []
  (p/set-profiles [{:name "default" :type "user" :value "foo"}]))

(defn prepare-params-with-hostname []
  (p/set-profiles [{:name "foo" :type "host" :value "foo-host"}]))


(def properties-stub (stub #'p/load-properties (constantly {"ex-url" "http://example.org"})))

(def username-stub (stub #'p/username (constantly "foo")))
(def hostname-stub (stub #'p/hostname (constantly "foo-host")))




(describe p/user-match?
	  (with [username-stub (before (prepare-params-with-username))]
		(given [param {:name "default" :type "user" :value "foo"}]		
		       (it "test if the username matches"
			   (#'p/user-match? param)))))


(describe p/host-match?
	  (with [hostname-stub (before (prepare-params-with-hostname))]
		(given [host "foo-host"
			param  {:type "host" :value host}]
		       (it "hostname should match local hostname"
			   (= host (#'p/hostname)))
		       (it "hostname should match given params"		     
			   (= (#'p/host-match? param))))))

(describe p/filter-by-rule
	  (with [username-stub (before (prepare-params-with-username))]
		(it "it should match with username params"
		    (= "foo" (:value (#'p/filter-by-rule)))))	  
	  (with [hostname-stub (before (prepare-params-with-hostname))]
		(it "it should match with hostname params"
		    (= "foo-host" (:value (#'p/filter-by-rule))))))


(describe p/my-profile
	  (with [username-stub (before (prepare-params-with-username))]
		(it "it should find a matching profile"
		    (= (p/my-profile) (first (prepare-params-with-username))))))

(describe p/all-properties
	  (with [properties-stub]
		(it "it should get all properties from profile"
		    (= (:ex-url (p/all-properties)) "http://example.org"))))