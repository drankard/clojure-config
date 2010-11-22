(ns clojure-config.core
  (:use clojure.contrib.logging)
  (:require [clojure.contrib.string :as string]
	    [clojure.contrib.properties :as p]
	    [clojure.walk :as w])
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
  (and (= (:type param) "host") (= (:value param) (hostname))))

(defn- user-match? [param]
  (and (= (:type param) "user") (= (:value param) (username))))

(defn- env-match? [param]
  (and (= (:type param) "env") (= (:value param) (env))))


(defn- get-property-files [rule]
  (let [name (:name rule)
	parent (:parent rule)]
    (let [ out
	  (if (not (nil? name))
	    (assoc {} :file (str name ".properties")))]
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
  (let [file (load-properties (:file files))
	parent (load-properties (:parent-file files))]
    (if (nil? (get file key))
      (get parent key)
      (get file key))))


;; Public functions



(defn set-profiles [profiles]
    (alter-var-root (var *profiles*)
		    (constantly profiles)))
  
(defn my-profile []
  (filter-by-rule))

(defn all-properties []
  (let [rule (filter-by-rule)
	files (get-property-files rule)]
    (w/keywordize-keys (merge (load-properties (:parent-file files)) (load-properties (:file files))))))



(defn get-property [key]
  (let [key-str (string/as-str key)
	rule (filter-by-rule)
	files (get-property-files rule)]
    (load-property key files)))
      
	



