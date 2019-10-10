package generator;

import database.DatabaseConnectionHandler;
import misc.Config;
import misc.Constants;
import misc.Utils;
import parsing.ParsingResult;

/**
 * Container class for everything related to generating a computation service.
 * @author Philip Kaiser
 *
 */
public class ServiceGenerator{
	
	Config config;
	DatabaseConnectionHandler dbHandler;
	String unalteredFunction;
	ParsingResult parsingResult;
	String targetRootDir;
	String serviceName;
	
	/**
	 * Container class for everything related to generating a computation service.
	 * @param function Arithmetic function the service evaluates
	 * @param parsingResult Result of the parser
	 * @param targetRootDir Root directory of location where all temporary files are to be created. (default: /app/generated/)
	 * @param serviceName Name of the service.
	 * @param config Configuration object
	 * @param dbHandler Database handler
	 */
	public ServiceGenerator(String function, ParsingResult parsingResult, String targetRootDir, String serviceName,
			Config config, DatabaseConnectionHandler dbHandler) {
		this.config = config;
		this.dbHandler = dbHandler;
		this.unalteredFunction = function;
		this.parsingResult = parsingResult;
		this.targetRootDir = targetRootDir;
		this.serviceName = serviceName;
	}

	/**
	 * Triggers the generation of the native HE code and the servlet code and their respective deployments.
	 * @return True, if successful.
	 */
	public boolean generateService() {
		String targetDir = Utils.appendTrailingSlash(targetRootDir) + serviceName + "/";
	
    	System.out.println("Generating he code...");
    	HELibCodeGenerator.generateHEService(parsingResult, 
    			targetDir + Constants.SERVICE_SERVLET_NATIVE_CODE_DIRECTORY_NAME + "/", serviceName, config.getTomcatPath());
    	
    	//TODO Instantiate instead of static method call?
    	System.out.println("Generating servlet ...");
    	double estimatedRuntime = parsingResult.getPredictor().getEstimatedRuntime();
		ServiceServletGenerator.generateServlet(serviceName, targetDir + Constants.SERVICE_SERVLET_RESOURCES_FOLDER + "/", parsingResult.getVariablesList(), 
				unalteredFunction, estimatedRuntime, parsingResult.getPredictor().needsBootstrapping(), dbHandler, config);
		System.out.println("Done generating servlet.");
		
		return true;
	}
}
