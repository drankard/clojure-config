(ns clojure-config.config-tests
  (:import (java.net InetAddress))
  (:require [clojure-config.core :as c])
  (:use [lazytest.context.stub]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))


(defn prepare-params-with-username []
  (c/set-profiles [{:name "default" :type "user" :value "foo"}]))

(defn prepare-params-with-hostname []
  (c/set-profiles [{:name "foo" :type "host" :value "foo-host"}]))


(def properties-stub (stub #'c/load-properties (constantly {"ex-url" "http://example.org"})))

(def username-stub (stub #'c/username (constantly "foo")))
(def hostname-stub (stub #'c/hostname (constantly "foo-host")))




(describe c/user-match?
	  (with [username-stub (before (prepare-params-with-username))]
		(given [param {:name "default" :type "user" :value "foo"}]		
		       (it "test if the username matches"
			   (#'c/user-match? param)))))


(describe c/host-match?
	  (with [hostname-stub (before (prepare-params-with-hostname))]
		(given [host "foo-host"
			param  {:type "host" :value host}]
		       (it "hostname should match local hostname"
			   (= host (#'c/hostname)))
		       (it "hostname should match given params"		     
			   (= (#'c/host-match? param))))))

(describe c/filter-by-rule
	  (with [username-stub (before (prepare-params-with-username))]
		(it "it should match with username params"
		    (= "foo" (:value (#'c/filter-by-rule)))))	  
	  (with [hostname-stub (before (prepare-params-with-hostname))]
		(it "it should match with hostname params"
		    (= "foo-host" (:value (#'c/filter-by-rule))))))


(describe c/my-profile
	  (with [username-stub (before (prepare-params-with-username))]
		(it "it should find a matching profile"
		    (= (c/my-profile) (first (prepare-params-with-username))))))

(describe c/all-properties
	  (with [properties-stub]
		(it "it should get all properties from profile"
		    (= (:ex-url (c/all-properties)) "http://example.org"))))