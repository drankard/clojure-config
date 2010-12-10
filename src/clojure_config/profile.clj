(ns clojure-config.profile)

(defmacro defprofiles [& body]
  `(do ~@(map #(list 'profile %) body)))

(defmacro profile [& body]
  `(do ~@body))


(comment (def tester
     (defprofiles
       (profile david (host "XPN1234")))))