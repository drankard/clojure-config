##Warning! still not stable..

# Clojure config

A small framework to load property config runtime by profiles

# Usage

The *clojure-config.core* namespace contains a global *\*profiles\** var, that needs to be initialized when starting the application.
This is done by calling set-profiles with the apps setup.

This can currently be by username: :type "user" or by hostname: :type "host"
there is an untested feature to get the profile from the OS enviroment set the *$ENV* variable and use: :type "env"

:type can be "user" "host" (soon "env")
:value has to be the resolved value, if you set :type to "user" then the :value has to match the current OS user, if "host" then :value has to match OS hostname.
:value it also the identifyer for a matching property file, that holds all the apps properties.

The :parent key is mapping to another profile, and properties from that profile will be available, note that if a property is mentioned in both the matching property and the parent property, the parent i overwritten.

globals:
if you add a profile called _global_ and add a property file or add a property map to it theese properties will be loaded in all profiles

###example:
	user=>	(use 'clojure-config.core)
	nil
	user=>	(set-properties [
			      {:name "global" :properties {:a_global "im acceable from all profiles"}}
			      {:name "test" :type "host" :value "test-hostname"}
			      {:name "production" :type "host" :value "prod-hostname"}
			      {:name "ci" :type "host" :value "ci-hostname"}
			      {:name "My Dev Profile" :type "user" :value "foo" :parent "test"}])


### new feature:
It is now possible to add properties directly in the profile, just use :properties keyword and add a map containing the properties needed
    (set-properties [{:name "test" :type "host" :value "test-hostname" :properties {:foo "bar"}}])
if there is a mathing property file it will still be loaded and merged.

(property "key") ; returns the value or nil
(properties)     ; returns all properties in a map
global properties added
