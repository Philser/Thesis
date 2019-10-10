This is the maven software project of the client component of the prototype.

Dependencies:
	This software requires Maven, NTL, GMP, HELib, and a C++-11 compatible compiler installed on your system.
	You can find the libraries under the links below.
	Build instructions for each library can be found under the respective link as well.
	
	Maven: https://maven.apache.org/download.cgi
	NTL: https://www.shoup.net/ntl/download.html
	GMP: https://gmplib.org/#DOWNLOAD
	HELib: https://github.com/shaih/HElib

Build:
	1. First, navigate to \src\main\java\homomorphic.
	2. Run make.
	3. Move the created .so file to a place convenient for you (we will need the path later).
	4. Navigate back to the root of the project.
	5. Run mvn clean install

Usage:
	After the build, the client executable can be found at HEServiceClient\target\he_service_client-0.0.1-SNAPSHOT-jar-with-dependencies.jar
	
	The client expects some configuration parameters at startup. These are:
		Djava.library.path - The path Java checks for when searching for native libraries. 
							 Specify the path where you put the.so file in building step 3.
		peerId - The ID of the peer this client should impersonate.
		target - The URL of the Cloud Foundry app.
		
	Example:
		java -Djava.library.path=. -jar he_service_client-0.0.1-SNAPSHOT-jar-with-dependencies.jar peerId=1 target=https://example.cfapps.sap.hana.ondemand.com