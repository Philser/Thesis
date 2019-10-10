This folder contains the software projects of the prototype developed as part of the 
Master's Thesis 'Generic Secure Multi-Party Computation in Centralised Cloud-based Environments' by Philip Kaiser.
Each project contains detailed descriptions on how to compile and run them.

These projects are:

	Docker_Image
		The Docker configuration file and necessary static files to create a Docker image necessary for the prototype to be run on the Cloud Foundry application platform.
	
	HEContextCreator
		A helper tool to create contexts and keys for HELib.
		
	HEServiceClient
		The client component of the prototype.
		
	HEServiceGenerator
		The service generator that creates and deploys computation services and is capable of communicating with clients.
			
	HEServiceGenerator_Eval
		An adapted version of the HEServiceGenerator project to conduct performance measurements.
	
	HEServiceServlet
		The static code a computation service is made of. This is a convenience project to work on the computation service's code.
		The project is meant to be zipped and placed under ...\HEServiceGenerator\src\main\java\resources\service_servlet\servlet.zip