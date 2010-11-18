##Warning! still not stable..

# Clojure config

A small api to load properties runtime by profiles

# Usage

The *property-profiles.properties* namespace contains a *\*profiles\** that needs to be initialized when starting the application.
This is done by calling set-profiles with the apps setup.

This can currently be by username: :type "user" or by hostname: :type "host"
there is an untested feature to get the profile from the OS enviroment set the *$ENV* variable and use: :type "env"

*:type can be "user" "host" (soon "env")*
*:value has to be the resolved value, if you set :type to "user" then the :value has to match the current OS user, if :host then :value has to match OS hostname.

	user=>	(use 'property-profiles.properties)
	nil
	user=>	(set-profiles [
			      {:name "default" :type "host" :value "my-hostname"}
			      {:name "My Profile" :type "user" :value "foo" :parent "default"}])



	
