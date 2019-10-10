This is the Docker image used to launch the prototype on the Cloud Foundry application platform.
You are required to have a Dockerhub account.
You can create one for free at https://hub.docker.com/

Before being able to build this image, a few more steps are necessary.

	1. A valid build of the HEServiceGenerator project is required. It is required to be in the WAR format and namend 'serviceGenerator.war' 
		(unless otherwise specified in the Dockerfile).
	2. A valid context and public key are required. These can be created using the HEContextCreator tool and have to be placed in the folder 
		under the names 'pubKey_bootstrappable.txt' and 'context_bootstrappable.txt' (unless otherwise specified in the Dockerfile).

To build the docker image, run the following commands inside a bash in this directory:

	1. docker build .
	2. Find the image ID of the built image with
		docker image ls
	3. docker tag IMAGE_ID DOCKERHUB_USER/IMAGE_NAME:TAG
	4. docker push DOCKERHUB_USER/IMAGE_NAME:TAG
	
	Note that you can replace the given libraries with other versions. Remember to make the appropriate changes to the Dockerfile file.

To run the image in Cloud Foundry, run:
	cf push YOUR_APP_NAME --docker-image DOCKERHUB_USER/IMAGE_NAME:TAG