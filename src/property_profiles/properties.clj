(ns property-profiles.properties
  (:use clojure.contrib.logging)
  (:require [clojure.contrib.string :as string]
	    [clojure.contrib.properties :as p])
  (:import (java.net InetAddress))
  (:import (java.io File FileNotFoundException)))

(def *profiles* {})

		  

;; System Calls
(defn- hostname []
  (let [addr (. InetAddress getLocalHost)]    
    (.getHostName addr)))

(defn- env []
  (System/getenv "ENV"))

(defn- username []
  (System/getProperty "user.name"))


(defn- host-match? [param]
  (and (= (:type param) "host") (= (:name param) (hostname))))

(defn- user-match? [param]
  (and (= (:type param) "user") (= (:name param) (username))))

(defn- env-match? [param]
  (and (= (:type param) "env") (= (:name param) (env))))


(defn get-property-files [rule]
  (prn rule)
  (let [name (:name rule)
	parent (:parent rule)]
    (prn name parent)
    (let [ out
	  (if (not (nil? name))
	    (assoc {} :file (str name ".properties")))]
      (prn out)
      (if (not (nil? parent))
	(assoc out :parent-file (str parent ".properties"))
	out))))


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
  (prn filename)
  (if (not (nil? filename))
    (let [resource (-> (Thread/currentThread)
		     (.getContextClassLoader)			
		     (.getResource filename))]
    (if (not (nil? resource))      
      (into {} (doto (java.util.Properties.)
		 (.load (-> (Thread/currentThread)
			    (.getContextClassLoader)			
			    (.getResourceAsStream filename)))))))))

(defn- load-property [key files]
  (prn key files)
  (let [file (load-properties (:file files))
	parent (load-properties (:parent-file files))]
    (if (nil? (get file key))
      (get parent key)
      (get file key))))


;; Public functions

(defn set-profiles [profiles]
    (alter-var-root (var *profiles*)
		    (constantly profiles)))
  




(defn get-property [key]
  (let [key-str (string/as-str key)
	rule (filter-by-rule)
	files (get-property-files rule)]
    (prn (str key-str  " " files " "  rule))
    
    (load-property key files)))
      
	



