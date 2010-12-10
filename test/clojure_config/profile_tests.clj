(ns clojure-config.profile-tests
  (:require [clojure-config.core :as c]
	    [clojure-config.profile])
  (:use [lazytest.context.stub]
	[lazytest.context.stateful :only (stateful-fn-context)]
	[lazytest.describe :only (describe before after it given do-it for-any with)]))



(comment (describe "test defprofiles macro"
  (given [out (defprofiles (prn "hello"))]
    (it "should call the macro and return a func"
      (= 1 1)))))