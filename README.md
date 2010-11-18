##Warning! still not stable..

# Clojure config

A small api to load properties runtime by profiles

# Usage

The *property-profiles.properties* namespace contains a *\*profiles\** that needs to be initialized when starting the application.
This is done by calling set-profiles with the apps setup.

This can currently be by username or by hostname. there is an untested feature to get the profile from the os throug enviroment var (ENV).

	user=>	(use 'property-profiles.properties)
	nil
	user=>	(set-profiles [
			      {:name "default" :type "host" :value "my-hostname"}
			      {:name "My Profile" :type "user" :value "foo" :parent "default"}])

</code>



	
