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


(defn prepare-params-with-properties []
  (c/set-profiles [{:name "foo" :type "host" :value "foo-host"
		    :properties {:foo-prop "bar"
				 :hello-prop "hello"}}]))

(defn cleanup []
    (c/set-profiles nil))


(def properties-stub (stub #'c/load-from-file (constantly {:ex-url "http://example.org"})))

(def username-stub (stub #'c/username (constantly "foo")))
(def hostname-stub (stub #'c/hostname (constantly "foo-host")))





(describe "load-profile"
  (with [hostname-stub
	 properties-stub
	 (before (prepare-params-with-properties))
	 (after (cleanup))]      
    (it "it should load the correct profile"
      (let [profile (c/load-profile)]
	(and
	 (= (:foo-prop profile) "bar")
	 (= (:ex-url profile) "http://example.org"))))))

  

(describe "c/user-match?"
  (with [username-stub
	 (before (prepare-params-with-username))
	 (after (cleanup))]
    (given [param {:name "default" :type "user" :value "foo"}]		
      (it "test if the username matches"
	(#'c/user-match? param)))))


(describe c/host-match?
  (with [hostname-stub
	 (before (prepare-params-with-hostname))
	 (after (cleanup))]
    (given [host "foo-host" param  {:type "host" :value host}]
      (it "hostname should match local hostname"
	(= host (#'c/hostname)))
      (it "hostname should match given params"		     
	(= (#'c/host-match? param))))))

(describe c/filter-by-profile
  (with [username-stub
	 (before (prepare-params-with-username))
	 (after (cleanup))]
    (it "it should match with username params"
      (= "foo" (:value (#'c/determin-profile)))))	  
  (with [hostname-stub
	 (before (prepare-params-with-hostname))
	 (after (cleanup))]
    (it "it should match with hostname params"
      (= "foo-host" (:value (#'c/determin-profile))))))


(describe c/my-profile
  (with [username-stub
	 (before (prepare-params-with-username))
	 (after (cleanup))]
    (it "it should find a matching profile"
      (= (c/my-profile) (first (prepare-params-with-username))))))
