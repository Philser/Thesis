This is the maven software project of the service generator component of the prototype.

Dependencies:
	This software requires Maven installed on your system.
	
	Maven: https://maven.apache.org/download.cgi

Build:
	1. Navigate to the root of the project.
	2. Run mvn clean install

Usage:
	After the build, the client executable can be found at HEServiceGenerator\target\serviceGenerator.war
	This .war is meant to be put into the Docker project.
	However, you can install this locally in a Linux environment as well.
	Therefore, make sure to:
	
		1. Have NTL, GMP, and HELib installed on your systems. You can find links for each library below.
			NTL: https://www.shoup.net/ntl/download.html
			GMP: https://gmplib.org/#DOWNLOAD
			HELib: https://github.com/shaih/HElib
			
		2. Have Tomcat installed an running.
			Tomcat: http://tomcat.apache.org/tomcat-9.0-doc/index.html
			
		3. Have the HESerivceGenerator's ProductiveConfig class adapted to your local configuration.
			You can find the class at  [...]\HEServiceGenerator\src\main\java\misc\ProductiveConfig.java
		
		4. Adapt the HESerivceGenerator's Tomcat path in the Constants class.
			You can find the class at [...]\HEServiceGenerator\src\main\java\misc\Constants.java
			There, adapt TOMCAT_WEBAPPS_ABSOLUTE_PATH variable to point to your local Tomcat's webapps directory.
		
		5. Deploy the built serviceGenerator.war in your local Tomcat's webapps directory.
		
		6. The service is now deployed and awaits requests.