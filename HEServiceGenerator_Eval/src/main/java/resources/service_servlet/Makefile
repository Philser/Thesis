maven:
	unzip -u servlet.zip
	cp ../native_code/org_example_HECodeInterface.so src/main/java/resources/native_code
	cp ../native_code/context.txt src/main/java/resources/native_code/
	cp ../native_code/pubKey.txt src/main/java/resources/native_code/
	mvn clean install -Djavax.net.ssl.trustStorePassword=changeit -DskipTests
	cp target/test.war /usr/share/tomcat8/webapps