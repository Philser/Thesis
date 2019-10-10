package misc;

import predictor.HELibContext;

public class Constants {

	public static final String SERVICE_GENERATION_TARGET_ROOT = "/app/generated/";
	
	public static final String HE_SERVICE_RESOURCES_FOLDER = "service/";
	public static final String HE_SERVICE_TEMPLATE_FOLDER = HE_SERVICE_RESOURCES_FOLDER + "templates/";
	public static final String HE_SERVICE_TEMPLATE_FILE_NAME = "he_code_template.stg";
	public static final String HE_SERVICE_MAKEFILE_TEMPLATE_NAME = "Makefile_template.stg";
	
	public static final String SERVICE_SERVLET_RESOURCES_FOLDER = "service_servlet/";
	public static final String SERVICE_SERVLET_TEMPLATE_FOLDER = SERVICE_SERVLET_RESOURCES_FOLDER + "templates/";
	public static final String SERVICE_SERVLET_POM_TEMPLATE_NAME = "pom_template.stg";
	public static final String SERVICE_SERVLET_POM_FILE_NAME = "pom.xml";
	public static final String SERVICE_SERVLET_ZIPPED_CODE_FILE_NAME = "servlet.zip";
	public static final String SERVICE_SERVLET_MAKEFILE_FILE_NAME = "Makefile";
	public static final String SERVICE_SERVLET_MAKEFILE_TEMPLATE_NAME = "makefile_template.stg";
	public static final String SERVICE_SERVLET_NATIVE_CODE_DIRECTORY_NAME = "native_code";
	public static final String SERVICE_SERVLET_CLIENT_DIRECTORY_NAME = "client";
	public static final String SERVICE_SERVLET_MAIN_SERVLET_FILE_NAME = "MainServlet.java";
	public static final String SERVICE_SERVLET_CLIENT_DISTRIBUTION_SERVLET_FILE_NAME = "ClientDistributionServlet.java";
	public static final String SERVICE_SERVLET_MAIN_SERVLET_TEMPLATE_NAME = "main_servlet_template.stg";
	public static final String SERVICE_SERVLET_CLIENT_DISTRIBUTION_SERVLET_TEMPLATE_NAME = "client_distribution_servlet_template.stg";
	public static final String SERVICE_SERVLET_HE_CODE_INTERFACE_TEMPLATE_NAME = "jni_class_template.stg";
	public static final String SERVICE_SERVLET_DB_HANDLER_TEMPLATE_NAME = "database_connection_handler_template.stg";
	public static final String SERVICE_SERVLET_HE_CODE_INTERFACE_FILE_NAME = "HECodeInterface.java";
	public static final String SERVICE_SERVLET_DB_HANDLER_FILE_NAME = "DatabaseConnectionHandler.java";
	public static final String SERVICE_SERVLET_RELATIVE_PATH_TO_SERVLETS_DIRECTORY = "src/main/java/thesis/servlets";
	public static final String SERVICE_SERVLET_RELATIVE_PATH_TO_DATABASE_DIRECTORY = "src/main/java/database";
	public static final String SERVICE_SERVLET_RELATIVE_PATH_TO_HE_CODE_INTERFACE_DIRECTORY = "src/main/java/jni";
	
	
	public static final String SERVICE_MAKEFILE_FILE_NAME = "Makefile";
	
	private static final String SERVICE_HE_LIBRARY_NAME = "he_functions";
	public static final String SERVICE_HE_LIBRARY_FILE_NAME = SERVICE_HE_LIBRARY_NAME + ".cpp";
	public static final String SERVICE_HE_LIBRARY_HEADER_FILE_NAME = SERVICE_HE_LIBRARY_NAME + ".h";
	
	private static final String SERVICE_JNI_INTERFACE_NAME = "org_example_HECodeInterface";
	public static final String SERVICE_JNI_INTERFACE_FILE_NAME = SERVICE_JNI_INTERFACE_NAME + ".cpp";
	public static final String SERVICE_JNI_INTERFACE_HEADER_FILE_NAME = SERVICE_JNI_INTERFACE_NAME + ".h";
	public static final String SERVICE_JNI_INTERFACE_LIBRARY_FILE_NAME = "HECodeInterface.so";
	
	public static final String CRYPTO_GLBOAL_FOLDER = "/app/crypto/";
	public static final String CRYPTO_RESOURCES_FOLDER = "crypto/";
	public static final String CRYPTO_BOOTSTRAPPING_PUBLIC_KEY_FILE_NAME = "pubKey_bootstrappable.txt";
	public static final String CRYPTO_PUBLIC_KEY_FILE_NAME = "pubKey.txt";
	public static final String CRYPTO_SECRET_KEY_FILE_NAME = "secKey.txt";
	public static final String CRYPTO_CONTEXT_FILE_NAME = "context.txt";
	public static final String CRYPTO_BOOTSTRAPPING_CONTEXT_FILE_NAME = "context_bootstrappable.txt";

	public static final String TOMCAT_WEBAPPS_ABSOLUTE_PATH = "/opt/tomcat/latest/webapps"; //"/opt/tomcat/latest/webapps"; "/usr/local/tomcat/webapps/"
	
	public static final String SERVICE_BASE_URL = "https://heservice.cfapps.sap.hana.ondemand.com/";
	
	//TODO Delete later
	public static final String TOMCAT_WEBAPPS_ABSOLUTE_PATH_DEV = "/usr/share/tomcat8/webapps";
	public static final String SERVICE_BASE_URL_DEV = "https://heservice.cfapps.sap.hana.ondemand.com/";
	
	public static final HELibContext ContextNonBootstrappable = new HELibContext(600.0, 27311, 2, 16, 109);
	public static final HELibContext ContextBootstrappable = new HELibContext(600.0, 35113, 2, 16, 115);

	public static final String SERVICE_HE_LIBRARY_MAKEFILE_NAME = "Makefile";

	public static final String CENTRAL_SERVICE_TABLE_NAME = "service";
	
}
