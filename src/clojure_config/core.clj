(ns clojure-config.core
  (:use clojure.contrib.logging)
  (:require [clojure.contrib.string :as string]
	    [clojure.contrib.properties :as p]
	    [clojure.walk :as w])
  (:import (java.net InetAddress))
  (:import (java.io File FileNotFoundException)))

(def *profiles* {})

		  

;; System Calls
(defn hostname []
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


(defn- get-property-files [profile]
  (let [value (:name profile)
	parent (:parent profile)]
    (let [ out
	  (if (not (nil? value))
	    (assoc {} :file (str value ".properties")))]
      (if (not (nil? parent))
	(assoc out :parent-file (str parent ".properties"))
	out))))



(defn- match-params? [current]
  (or (user-match? current)
      (host-match? current)
      (env-match? current)))



(defn determin-profile []
  (first (filter (fn [p] (match-params? p)) *profiles*)))

  
(defn- determin-profile-1  []
  (loop [the-rest *profiles*]
    (let [current (first the-rest)]
      (prn current)
      (if (or (empty? the-rest) (match-params? current))
	current
	(recur (rest the-rest))))))




(defn- load-from-file [filename]  
  (w/keywordize-keys (if (not (nil? filename))
    (let [resource (-> (Thread/currentThread)
		     (.getContextClassLoader)			
		     (.getResource filename))]
    (if (not (nil? resource))      
      (into {} (doto (java.util.Properties.)
		 (.load (-> (Thread/currentThread)
			    (.getContextClassLoader)			
			    (.getResourceAsStream filename))))))))))



(defn load-profile []
  (let [profile (determin-profile)
	files (get-property-files profile)]    
    (conj (:properties profile)
	  (load-from-file (:parent-file files))
	  (load-from-file (:file files)))))



(defn- load-property [key files]
  (let [file (load-from-file (:file files))
	parent (load-from-file (:parent-file files))]    
    (if (nil? (get file key))
      (get parent key)
      (get file key))))



;; Public functions



(defn set-profiles [profiles]
    (alter-var-root (var *profiles*)
		    (constantly profiles)))
  
(defn my-profile []
  (determin-profile))

(defn properties []
  (load-profile))

(defn property [key]
  ((keyword key)(load-profile)))
