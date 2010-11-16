(defproject property-profiles "1.0.0-SNAPSHOT"
  :description "Property profiles"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]]


  :dev-dependencies [[swank-clojure "1.2.1"]]

  :property-profiles [
		      {:name "m14758" :type "user" :parent "ci"}
		      {:name "ci" :type "host"}
		      {:name "XPN55422" :type "host"}]
  )
