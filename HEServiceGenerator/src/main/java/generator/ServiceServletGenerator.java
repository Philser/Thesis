package generator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import database.DatabaseConnectionHandler;
import misc.Config;
import misc.Constants;
import misc.Utils;

/**
 * Generator of the service servlet.
 * @author Philip Kaiser
 *
 */
public class ServiceServletGenerator {

	/**
	 * Triggers generation and deployment of servlet code.
	 * @param serviceName Name of the service to be created
	 * @param targetDir Target directory for the generated code
	 * @param varList List of all variables contained in the arithmetic function
	 * @param origFunction Original arithmetic function as given by the user
	 * @param estimatedRuntime Estimation of a service's function evaluation run
	 * @param needsBootstrapping Whether or not the evaluation needs boostrapping
	 * @param dbHandler Databsae handler
	 * @param config Configuration object
	 */
	public static void generateServlet(String serviceName, String targetDir, List<String> varList, String origFunction,
			double estimatedRuntime, boolean needsBootstrapping, DatabaseConnectionHandler dbHandler, Config config) {
		try {
			//Create Tables
			String function = origFunction;
			createTables(serviceName, function, "" + estimatedRuntime + "s", dbHandler, needsBootstrapping);
			
			//Generate code for app
			generateServletCode(serviceName, targetDir, varList, config);
									
			//Compile code
			deployServlet(targetDir);
//			fetchAndDeployCryptoFiles(serviceName, config.getKeyServiceURL(), needsBootstrapping);
		}
		catch(Exception e) {
			File log = new File(targetDir + "error.txt");
			e.printStackTrace();
			try {
				log.createNewFile();
				PrintWriter writer = new PrintWriter(log, "UTF-8");
				e.printStackTrace(writer);
				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private static void createTables(String serviceName, String function, String estimatedRuntime, DatabaseConnectionHandler dbHandler, 
			boolean needsBootstrapping) 
			throws SQLException {
		dbHandler.createServiceTables(serviceName, function, estimatedRuntime, needsBootstrapping);
	}

	/**
	 * Triggers execution of a makefile to build and deploy code.
	 * @param targetDir Directory containing the makefile
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws Exception
	 */
	private static void deployServlet(String targetDir) throws InterruptedException, IOException, Exception {
		ProcessBuilder ps = new ProcessBuilder("make", "-C", targetDir);

		//From the DOC:  Initially, this property is false, meaning that the 
		//standard output and error output of a subprocess are sent to two 
		//separate streams
		ps.redirectErrorStream(true);

		Process pr = ps.start();  
		
		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
		    System.out.println(line);
		}
		int result = pr.waitFor();
		System.out.println("ok!");

		in.close();
		if(result == 0) {
			System.out.println("Build successful");
		} else {
			throw new Exception("Service build unsuccessful");
		}
		
	}

	/**
	 * Triggers servlet code creation and static file deployment
	 * @param serviceName Name of the service to be created
	 * @param targetDir Target directory to deploy files to
	 * @param varList List of variables contained in the arithmetic function - Needed for code generation
	 * @param config Configuration object
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void generateServletCode(String serviceName, String targetDir, List<String> varList, Config config) 
			throws IOException, URISyntaxException {
		generateServletCodeFromTemplate(serviceName, targetDir, varList, config);
		
		// deploy zipped servlet and pom
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		
		URI zippedServletCodePath = loader.getResource(Constants.SERVICE_SERVLET_RESOURCES_FOLDER 
				+ Constants.SERVICE_SERVLET_ZIPPED_CODE_FILE_NAME).toURI();
		File zippedServlet = new File(zippedServletCodePath);
		File newZippedServlet = new File(targetDir + zippedServlet.getName());
		
		Utils.deployStaticResource(zippedServlet, newZippedServlet);
	}
	
	/**
	 * Triggers the dynamic generation of all code files that rely on templates sources.
	 * @param serviceName Name of the service to be generated
	 * @param targetDir Target deployment directory
	 * @param varList List of variables found in the arithmetic function
	 * @param config Configuration object
	 */
	private static void generateServletCodeFromTemplate(String serviceName, String targetDir, List<String> varList, Config config) {
		generatePomTemplate(serviceName, targetDir);
		generateMainServletClass(
				targetDir + Utils.appendTrailingSlash(Constants.SERVICE_SERVLET_RELATIVE_PATH_TO_SERVLETS_DIRECTORY),
				varList,
				serviceName);
		generateDbConnectionHandler(targetDir + Utils.appendTrailingSlash(Constants.SERVICE_SERVLET_RELATIVE_PATH_TO_DATABASE_DIRECTORY),config, serviceName);
		generateHECodeInterface(serviceName, targetDir + 
				Utils.appendTrailingSlash(Constants.SERVICE_SERVLET_RELATIVE_PATH_TO_HE_CODE_INTERFACE_DIRECTORY));
		generateMakefile(serviceName, targetDir);
	}
	
	
	private static void generateDbConnectionHandler(String targetDir, Config config, String serviceName) {
		String targetFile = Utils.appendTrailingSlash(targetDir) + Constants.SERVICE_SERVLET_DB_HANDLER_FILE_NAME;
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL dbHandlerTemplateUrl = loader.getResource(Constants.SERVICE_SERVLET_TEMPLATE_FOLDER 
				+ Constants.SERVICE_SERVLET_DB_HANDLER_TEMPLATE_NAME);
		
		STGroup stg = new STGroupFile(dbHandlerTemplateUrl, "UTF-8", '$', '$');
		ST st = stg.getInstanceOf("db_handler");
		st.add("url", config.getDbUrl());
		st.add("user", config.getUser());
		st.add("password", config.getPassword());
		st.add("globalScheme", config.getSchema());
		st.add("servicesTableName", Constants.CENTRAL_SERVICE_TABLE_NAME);
		st.add("serviceName", serviceName);
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
		
	}

	private static void generateHECodeInterface(String serviceName, String targetDir) {
		String targetFile = Utils.appendTrailingSlash(targetDir) + Constants.SERVICE_SERVLET_HE_CODE_INTERFACE_FILE_NAME;
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL interfaceFileUrl = loader.getResource(Constants.SERVICE_SERVLET_TEMPLATE_FOLDER 
				+ Constants.SERVICE_SERVLET_HE_CODE_INTERFACE_TEMPLATE_NAME);
		
		STGroup stg = new STGroupFile(interfaceFileUrl, "UTF-8", '$', '$');
		ST st = stg.getInstanceOf("interface");
		st.add("path_to_lib", 
				Utils.appendTrailingSlash(Constants.TOMCAT_WEBAPPS_ABSOLUTE_PATH) 
				+ Utils.appendTrailingSlash(serviceName) 
				+ "WEB-INF/classes/" 
				+ Constants.SERVICE_SERVLET_NATIVE_CODE_DIRECTORY_NAME
				+ "/"
				+ serviceName + "_" +  Constants.SERVICE_JNI_INTERFACE_LIBRARY_FILE_NAME);
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
		
	}

	private static void generateMakefile(String serviceName, String targetDir) {
		//place main servlet in src/main/java/...
		String targetFile = Utils.appendTrailingSlash(targetDir) + Constants.SERVICE_SERVLET_MAKEFILE_FILE_NAME;
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL makefileTemplateUrl = loader.getResource(Constants.SERVICE_SERVLET_TEMPLATE_FOLDER + Constants.SERVICE_SERVLET_MAKEFILE_TEMPLATE_NAME);
		
		//Add path to shared library to java source file
		STGroup stg = new STGroupFile(makefileTemplateUrl, "UTF-8", '$', '$');
		ST st = stg.getInstanceOf("make_file");
		st.add("service_name", serviceName);
		st.add("native_interface_lib_name", serviceName + "_" + Constants.SERVICE_JNI_INTERFACE_LIBRARY_FILE_NAME);
		st.add("native_code_dirname", Constants.SERVICE_SERVLET_NATIVE_CODE_DIRECTORY_NAME);
		st.add("tomcat_webapps_path", Constants.TOMCAT_WEBAPPS_ABSOLUTE_PATH);

		String result = st.render();
		
		Utils.writeToFile(result, targetFile);		
	}

	private static void generateMainServletClass(String targetDir, List<String> varList, String serviceName) {
		//place main servlet in src/main/java/...
		String targetFile = Utils.appendTrailingSlash(targetDir) + Constants.SERVICE_SERVLET_MAIN_SERVLET_FILE_NAME;
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL servletFileUrl = loader.getResource(Constants.SERVICE_SERVLET_TEMPLATE_FOLDER + Constants.SERVICE_SERVLET_MAIN_SERVLET_TEMPLATE_NAME);
		
		//Add path to shared library to java source file
		STGroup stg = new STGroupFile(servletFileUrl, "UTF-8", '$', '$');
		ST st = stg.getInstanceOf("main_servlet");
		st.add("variables", varList);
		st.add("serviceName", serviceName);
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
		
	}


	private static void generatePomTemplate(String serviceName, String targetDir) {
		// place pom in project root
		String targetFile = Utils.appendTrailingSlash(targetDir) + Constants.SERVICE_SERVLET_POM_FILE_NAME;
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL pomTemplateUrl = loader.getResource(Constants.SERVICE_SERVLET_TEMPLATE_FOLDER + Constants.SERVICE_SERVLET_POM_TEMPLATE_NAME);
		
		STGroup stg = new STGroupFile(pomTemplateUrl, "UTF-8", '$', '$');
		ST st = stg.getInstanceOf("pomTemplate");
		st.add("serviceName", serviceName);
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
	}
	
	private static void fetchAndDeployCryptoFiles(String serviceName, String keyServiceUrl, boolean needsBootstrapping) {
		System.out.println("Downloading key...");
		String url = keyServiceUrl + "?needsBootstrapping=";
		url += needsBootstrapping ? "true" : "false";
		String targetDir = "/app/crypto/" + serviceName + "/";
		String targetFile = targetDir + "crypto.zip";
		File td = new File(targetDir);
		td.mkdirs();
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
		    byte dataBuffer[] = new byte[1024];
		    int bytesRead;
		    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
		        fileOutputStream.write(dataBuffer, 0, bytesRead);
	    	}
		    deployCryptoFiles(serviceName, targetFile);
		    System.out.println("Downloaded key.");
		} catch (IOException e) {
			System.out.println("Error fetching crypto files");
			e.printStackTrace();
		}
	}
	
	private static void deployCryptoFiles(String serviceName, String zipFileName) {
		try {
			File zipFile = new File(zipFileName);
			File destDir = new File("/app/crypto/" + serviceName + "/");
	        byte[] buffer = new byte[1024];
	        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	        ZipEntry zipEntry = zis.getNextEntry();
	        while (zipEntry != null) {
	            File newFile = createNewFile(destDir, zipEntry);
	            FileOutputStream fos = new FileOutputStream(newFile);
	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	            }
	            fos.close();
	            zipEntry = zis.getNextEntry();
	        }
	        zis.closeEntry();
	        zis.close();
	        
	        zipFile.delete();
		}
		catch(IOException e) {
			System.out.println("Error deploying crypto files");
			e.printStackTrace();
		}
	}
	
	private static File createNewFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
         
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
         
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException();
        }
         
        return destFile;
    }
}
