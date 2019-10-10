package generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import misc.Constants;
import misc.Utils;
import parsing.ParsingResult;
import predictor.HELibPredictor;

/**
 * Generates HELib specific code.
 * @author Philip Kaiser
 *
 */
public class HELibCodeGenerator {

	/**
	 * Triggers the generation of code.
	 * @param parsingResult Result of the parser
	 * @param targetDir Target the code is ought to be deployed to
	 * @param serviceName Name of the service
	 * @param tomcatDir Path to Tomcat's webapps directory
	 */
	public static void generateHEService(ParsingResult parsingResult, String targetDir, String serviceName, String tomcatDir)  {
		try {    	
	    	if(!targetDir.endsWith("/")) {
				targetDir += "/";
			}
	    	
			//Generate code for app
			generateHECode(parsingResult, targetDir, serviceName, tomcatDir);
			
			deployStaticHEResources(targetDir, ((HELibPredictor)parsingResult.getPredictor()).needsBootstrapping());
		
			//Compile code
			deployHECode(targetDir);
		}
		catch(Exception e) {
			File log = new File(targetDir + "error.txt");
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
	

	

	/**
	 * Triggers the execution of the makefile. This includes building code and deploying the result.
	 * @param targetDir Directory of the makefile.
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws Exception
	 */
	private static void deployHECode(String targetDir) throws InterruptedException, IOException, Exception {
		// Build library
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
//		int result = Runtime.getRuntime().exec("make -C " + targetDir).waitFor();
		if(result == 0) {
			System.out.println("Build successful");
		} else {
			throw new Exception("Service build unsuccessful");
		}
	}
	
	/**
	 * Triggers dynamic generation of files using templates and deploys static files.
	 * @param parsingResult Result of the parser
	 * @param targetDir Target directory to put the generated code
	 * @param serviceName Name of the service to be created
	 * @param tomcatDir Location of Tomcat's webapps directory
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void generateHECode(ParsingResult parsingResult, String targetDir, String serviceName, String tomcatDir) throws IOException, URISyntaxException {
		    	
		generateHECodeFromTemplate(parsingResult, targetDir + Constants.SERVICE_HE_LIBRARY_FILE_NAME, serviceName, tomcatDir);
		generateMakefileFromTemplate(serviceName, targetDir + Constants.SERVICE_HE_LIBRARY_MAKEFILE_NAME);

		//header is a static file 
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		
		URI jniInterfaceHeaderFilePath = loader.getResource(Constants.HE_SERVICE_RESOURCES_FOLDER 
				+ Constants.SERVICE_HE_LIBRARY_HEADER_FILE_NAME).toURI();
		File origJniInterfaceHeaderFile = new File(jniInterfaceHeaderFilePath);
		File newJniInterfaceHeaderFile = new File(targetDir + origJniInterfaceHeaderFile.getName());
		
		Utils.deployStaticResource(origJniInterfaceHeaderFile, newJniInterfaceHeaderFile);
	}
	
	/**
	 * Creates a makefile injecting values into a template.
	 * @param serviceName Name of the service to be created
	 * @param targetFile Name of the makefile to be created
	 */
	private static void generateMakefileFromTemplate(String serviceName, String targetFile) {
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URL makeFileTemplateUrl = loader.getResource(Constants.HE_SERVICE_TEMPLATE_FOLDER + Constants.HE_SERVICE_MAKEFILE_TEMPLATE_NAME);

		
		STGroup impl = new STGroupFile(makeFileTemplateUrl, "UTF-8", '$', '$');
		ST st = impl.getInstanceOf("make_file");
		st.add("service_name", serviceName);		
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
	}


	/**
	 * Generates HE code using a template
	 * @param parsingResult Result of the parser.
	 * @param targetFile Name of the target file to be created
	 * @param serviceName Name of the service to be created
	 * @param tomcatDir Location of Tomcat's webapps directory
	 */
	private static void generateHECodeFromTemplate(ParsingResult parsingResult, String targetFile, String serviceName, String tomcatDir) {
		List<String> variables = parsingResult.getVariablesList();
    	List<String> functionCodeLines = parsingResult.getGeneratedCode();
    	Map<String, String> constants = parsingResult.getConstantsList();
    	String functionCode = String.join("\n", functionCodeLines);
		
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		HELibPredictor predictor = (HELibPredictor) parsingResult.getPredictor();
		URL codeTemplateUrl = loader.getResource(Constants.HE_SERVICE_TEMPLATE_FOLDER + Constants.HE_SERVICE_TEMPLATE_FILE_NAME);

		
		STGroup impl = new STGroupFile(codeTemplateUrl, "UTF-8", '$', '$');
		ST st = impl.getInstanceOf("service");
		st.add("variables", variables);
		st.add("functionCode", functionCode);
		st.add("constantsMap", constants);
		st.add("levelThreshold", predictor.getLevelThreshold());
		
		/*
		 * The key files are stored at a central place available for all services because they are quite big
		 * */
		//String pathToContextFile =  Utils.appendTrailingSlash(Constants.CRYPTO_GLBOAL_FOLDER);
		//String pathToPubKeyFile = Utils.appendTrailingSlash(Constants.CRYPTO_GLBOAL_FOLDER);
		String pathToContextFile = "/app/crypto/";//"/mnt/hgfs/Workspace/cppPlayground/he_context_creator/";
		String pathToPubKeyFile = "/app/crypto/"; //"/mnt/hgfs/Workspace/cppPlayground/he_context_creator/";
		
		if(predictor.needsBootstrapping()) {
			pathToPubKeyFile += Constants.CRYPTO_BOOTSTRAPPING_PUBLIC_KEY_FILE_NAME;
			pathToContextFile += Constants.CRYPTO_BOOTSTRAPPING_CONTEXT_FILE_NAME;
		}
		else {
			pathToPubKeyFile += Constants.CRYPTO_PUBLIC_KEY_FILE_NAME;
			pathToContextFile += Constants.CRYPTO_CONTEXT_FILE_NAME;
		}
		 		
		st.add("path_to_context_file", pathToContextFile);
		st.add("path_to_public_key", pathToPubKeyFile);

		
		String result = st.render();
		
		Utils.writeToFile(result, targetFile);
	}
	
    /**
     * Deploys various static files.
     * @param targetDir Directory to deploy the files to
     * @param bootstrappingEnabled Whether or not bootstrapping is needed
     * @throws URISyntaxException
     * @throws IOException
     */
    private static void deployStaticHEResources(String targetDir, boolean bootstrappingEnabled) throws URISyntaxException, IOException{
    	
		ClassLoader loader = ServiceGenerator.class.getClassLoader();
		URI jniInterfaceFilePath = loader.getResource(Constants.HE_SERVICE_RESOURCES_FOLDER 
				+ Constants.SERVICE_JNI_INTERFACE_FILE_NAME).toURI();
		URI jniInterfaceHeaderFilePath = loader.getResource(Constants.HE_SERVICE_RESOURCES_FOLDER 
				+ Constants.SERVICE_JNI_INTERFACE_HEADER_FILE_NAME).toURI();
		
		
		
		File origJniInterfaceFile = new File(jniInterfaceFilePath);
		File newJniInterfaceFile = new File(targetDir + origJniInterfaceFile.getName());
		
		File origJniInterfaceHeaderFile = new File(jniInterfaceHeaderFilePath);
		File newJniInterfaceHeaderFile = new File(targetDir + origJniInterfaceHeaderFile.getName());
						
		    		
		Utils.deployStaticResource(origJniInterfaceFile, newJniInterfaceFile);
		Utils.deployStaticResource(origJniInterfaceHeaderFile, newJniInterfaceHeaderFile);
    }
	
}
