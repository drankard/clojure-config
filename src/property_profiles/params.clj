(ns property-profiles.params
  (:require [clojure.contrib.string :as string]
	    [clojure.contrib.properties :as p])
  (:import (java.net InetAddress))
  (:import (java.io File)))

(def *profiles* [])


(defn- hostname []
  (let [addr (. InetAddress getLocalHost)]    
    (.getHostName addr)))

(defn- env []
  (System/getenv "ENV"))

(defn- username []
  (System/getProperty "user.name"))

(defn- host-match? [param]
  (and (= (:type param) "host") (= (:name param) (hostname))))

(defn- user-match? [m]
  (and (= (:type m) "user") (= (:name m) (username))))

(defn- env-match? [m]
  (and (= (:type m) "env") (= (:name m) (env))))






(defn- get-property-file [rule]
  (let [name (:name rule)]
	(if (not (empty? name))
	  (str name ".properties"))))

(defn- get-parent-property-file [rule]
  (let [parent (:parent rule)]
    (if (not (empty? parent))
      (str parent ".properties"))))


(defn- match-params? [current]
  (or (user-match? current)
      (host-match? current)
      (env-match? current)))


(defn- filter-by-rule []
  (loop [the-rest *profiles*]
    (let [current (first the-rest)]
      (if (or (empty? the-rest) (match-params? current))
	current
	(recur (rest the-rest))))))




(defn- load-properties [filename]
  (let [resource (-> (Thread/currentThread)
		     (.getContextClassLoader)			
		     (.getResourceAsStream filename))]
    (if (not (nil? resource))      
      (into {} (doto (java.util.Properties.)
		 (.load (-> (Thread/currentThread)
			    (.getContextClassLoader)			
			    (.getResourceAsStream filename))))))))

(defn get-property [key]
  (let [key-str (string/as-str key)
	rule (filter-by-rule)
	property (get (load-properties (get-property-file rule)) key-str)
	parent-file (get-parent-property-file rule)]
    (if (and (empty? property) (not(empty? parent-file)))
      (get (load-properties parent-file) key-str)
      property)))
	     
      
	



