(ns clojure-config.config-tests
  (:import (java.net InetAddress))
  (:require [clojure-config.core :as c])
  (:use [lazytest.context.stub]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))


(defn prepare-params-with-username []
  (c/set-properties [{:name "default" :type "user" :value "foo"}]))

(defn prepare-params-with-hostname []
  (c/set-properties [{:name "foo" :type "host" :value "foo-host"}]))


(defn prepare-params-with-properties []
  (c/set-properties [{:name "foo"
		    :type "host"
		    :value "foo-host"
		    :properties {:foo-prop "bar"
				 :hello-prop "hello"}}
		   {:name "child"
		    :type "host"
		    :value "child-host"
		    :parent "foo"
		    :properties {:child-prop "hello child"}}]))

(defn cleanup []
    (c/set-properties nil))


(def properties-stub (stub #'c/load-from-file (constantly {:ex-url "http://example.org"})))

(def foo-user-stub (stub #'c/username (constantly "foo")))
(def foo-host-stub (stub #'c/hostname (constantly "foo-host")))
(def child-host-stub (stub #'c/hostname (constantly "child-host")))

(describe c/host-match?
  (with [foo-host-stub
	 (before (prepare-params-with-hostname))
	 (after (cleanup))]
    (given [host "foo-host" param  {:type "host" :value host}]
      (it "hostname should match local hostname"
	(= host (#'c/hostname)))
      (it "hostname should match given params"		     
	(= (#'c/host-match? param))))))

(describe c/properties
  (with [foo-host-stub
	 properties-stub
	 (before (prepare-params-with-properties))
	 (after (cleanup))]
    (it "is should load all properties"
      (let [properties (c/properties)]
	(and 
	 (= (:ex-url properties) "http://example.org")
	 (= (:foo-prop properties) "bar")
	 (= (:hello-prop properties ) "hello"))))))
  

(describe c/property
  (with [child-host-stub
	 properties-stub
	 (before (prepare-params-with-properties))
	 (after (cleanup))]
    (it "it should load all properties one at the time"
      (and
       (= (c/property :ex-url) "http://example.org")
       (= (c/property :child-prop) "hello child")))
    (it "it should also load properties from parent"
       (= (c/property :foo-prop) "bar"))))
  
