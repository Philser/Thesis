package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import antlr.RecognitionException;
import communication.AvailableServicesResponse;
import communication.CreateServiceRequest;
import communication.ErrorResponse;
import communication.CreateServiceResponse;
import communication.DeleteServiceRequest;
import communication.DeleteServiceResponse;
import database.ComputationServiceItem;
import database.DatabaseConnectionHandler;
import generator.AsyncGenerator;
import generator.ServiceGenerator;
import misc.Config;
import misc.Constants;
import misc.LocalConfig;
import misc.ProductiveConfig;
import misc.Utils;
import parsing.FunctionLexer;
import parsing.HELibParser;
import parsing.OperatorPrecedenceParser;
import parsing.ParsingResult;
import parsing.ThrowingErrorListener;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servlet that serves as entry point for the whole application and contains 
 * all functionality of the HE Service Generator application
 * 
 * @author D072531 - Philip Kaiser
 *
 */
public class ServiceGenerationServlet extends JsonServlet {

	private static final long serialVersionUID = 1L;
	private Map<String, Integer> variablesMap = new HashMap<String, Integer>();
	private Config config = new LocalConfig();
	//private Config config = new ProductiveConfig();
	private DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler(config.getDbUrl(), config.getUser(), 
			config.getPassword(), config.getSchema(), true);
	
	@Override
	public void init() throws ServletException {
		// Check if service info tables are present
		// And if not, initialize
		cleanUpTables(dbHandler);
		
		if(!dbHandler.serviceGeneratorIsInitialized())
			dbHandler.initServiceGeneratorTables();
	}
	
	//TODO: Delete when done
	private void cleanUpTables(DatabaseConnectionHandler dbHandler) {
		List<String> schemaNames = new ArrayList<>();
		schemaNames.add(config.getSchema());
		schemaNames.add("service");
		schemaNames.add("service2");
		dbHandler.cleanupDb(schemaNames);
	}


	@Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
    {		
		try {
    		handleGet(request, response);
    	}
    	catch(Exception ioe) {
    		ioe.printStackTrace();
    		ErrorResponse err = new ErrorResponse("Internal error", "Something went wrong. Please contact your administrator.");
			sendJsonResponse(response, err, 500);
    	}	
    }
	
	@Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
    	try {
    		handlePost(request, response);
    	}
    	catch(ParseCancellationException pce) {
    		System.out.println("Caught parsing error. Sending error response");
    		ErrorResponse err = new ErrorResponse("Parsing Error", "Could not parse given function.");
            sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
    	}
    	catch(JsonSyntaxException jse) {
    		String errorMessage = "Invalid JSON request. Required fields: ";
    		CreateServiceRequest req = new CreateServiceRequest("{value}", "{value}");
    		Gson g = new Gson();
    		errorMessage += g.toJson(req);
    		ErrorResponse err = new ErrorResponse("Parsing Error", errorMessage);
            sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
    	}
    	catch(Exception ioe) {
    		ioe.printStackTrace();
    		ErrorResponse err = new ErrorResponse("Internal Error", "Something went wrong. Please contact your administrator.");
            sendJsonResponse(response, err, 500);
    	}	
    }
    
	@Override
	protected void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
		try {
    		
    	}
    	catch(JsonSyntaxException jse) {
    		String errorMessage = "Invalid JSON request. Required fields: ";
    		CreateServiceRequest req = new CreateServiceRequest("{value}", "{value}");
    		Gson g = new Gson();
    		errorMessage += g.toJson(req);
    		ErrorResponse err = new ErrorResponse("Parsing Error", errorMessage);
            sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
    	}
    	catch(Exception ioe) {
    		ioe.printStackTrace();
    		ErrorResponse err = new ErrorResponse("Internal Error", "Something went wrong. Please contact your administrator.");
            sendJsonResponse(response, err, 500);
    	}	
	}

	/** 
	 * Handles all GET requests by sending a response containing a list of available services.
	 * @param request Request object
	 * @param response Response object
	 * @throws SQLException If there was an error with the database
	 * @throws IOException If there was an error sending the response
	 */
	private void handleGet(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		ResultSet result = dbHandler.getServices();
		List<ComputationServiceItem> services = new ArrayList<>();
		if(result != null) {
			while(result.next()) {
				String serviceName = result.getString(1);
				String function = result.getString(2);
				boolean isReady = result.getBoolean(3);
				String estimatedRuntime = result.getString(4);
				boolean needsBootstrapping = result.getBoolean(5);
				ComputationServiceItem serviceItem = new ComputationServiceItem(serviceName, function, isReady, estimatedRuntime, needsBootstrapping);
				services.add(serviceItem);
			}
		}
		
		AvailableServicesResponse respObj = new AvailableServicesResponse(services);
		sendJsonResponse(response, respObj, HttpServletResponse.SC_OK);
	}


	/**
	 * Handles POST requests by creating the requested service with the given name and function or throws an error
	 * in case the request is not valid.
	 *  
	 * @param request Request object
	 * @param response Response object
	 * @throws IOException If there was an error sending the response
	 * @throws RecognitionException If there was an error parsing the function
	 * @throws JsonSyntaxException If there was an error parsing the request
	 * @throws Exception Unpredicted errors
	 */
	private void handlePost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, RecognitionException, JsonSyntaxException, Exception {	
				
		String uri = request.getRequestURI();
    	if(uri.endsWith("/"))
    		uri = uri.substring(0, uri.length() - 2);
    	String[] segments = request.getRequestURI().split("/");
		String endpoint = segments[segments.length - 1];
		if(endpoint.equals("deleteService")) {
			handleDelete(request, response);
			return;
		}
	
		CreateServiceRequest csReq = parseRequest(request, CreateServiceRequest.class);
		String function = csReq.getFunction();
		//TODO: sanitize service name
		String serviceName = csReq.getServiceName();
		boolean serviceExists = dbHandler.serviceExists(serviceName);
		if(serviceExists) {
			ErrorResponse err = new ErrorResponse("Service Creation Error", "Service already exists");
			sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
		}
		else {	            
			ParsingResult parsingResult = parseFunction(function); //Translate function into library code
            
            String targetDir = Constants.SERVICE_GENERATION_TARGET_ROOT;
            String buildFilesDirectory = targetDir + serviceName;
            ServiceGenerator serviceGenerator = new ServiceGenerator(function, parsingResult, targetDir, serviceName, config, dbHandler);
            AsyncGenerator generator = new AsyncGenerator(buildFilesDirectory, serviceGenerator, dbHandler);
            Thread t = new Thread(generator);
            t.start();
            
            boolean needsBootstrapping = parsingResult.getPredictor().needsBootstrapping();
            double estimatedRuntime = parsingResult.getPredictor().getEstimatedRuntime();
            CreateServiceResponse scResp = new CreateServiceResponse(Utils.appendTrailingSlash(Constants.SERVICE_BASE_URL) + serviceName + "/", 
            		estimatedRuntime, needsBootstrapping);
            sendJsonResponse(response, scResp, HttpServletResponse.SC_OK);
		}
	}			  		
	
	/**
	 * Parses the given function
	 * 
	 * @param function The function to be parsed
	 * @return {@link ParsingResult}
	 * @throws ParseCancellationException If the given function is not valid
	 */
	private ParsingResult parseFunction(String function) throws ParseCancellationException 
    {
    	
		FunctionLexer lexer = new FunctionLexer(CharStreams.fromString(function));
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			
		HELibParser parser = new HELibParser();
		ParsingResult parsingResult = parser.parseTokenStream(tokenStream);
		parsingResult.setFunction(function);
		setFunctionVariables(parsingResult);
		
		System.out.println("Done");
    	return parsingResult;
    }	
    
    private void setFunctionVariables(ParsingResult parsingResult) {
		for(String s : parsingResult.getVariablesList())
			this.variablesMap.put(s, null);
	}
    
    /**
     * Helper function to parse incoming JSON requests to the respective Java POJO
     *  
     * @param request Request object
     * @param classType Target class
     * @return Instance of the target class
     * @throws IOException Error reading the request
     * @throws JsonSyntaxException Error parsing the JSON string
     */
    private <T> T parseRequest(HttpServletRequest request, Class<T> classType) throws IOException, JsonSyntaxException {
		String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		Gson g = new Gson();
        T jsonResponse = g.fromJson(payload, classType);
		return jsonResponse;
	}
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
    		throws IOException, JsonSyntaxException, Exception {
    	DeleteServiceRequest delReq = parseRequest(request, DeleteServiceRequest.class);

		String serviceName = delReq.getServiceName();
		List<String> schemas = new ArrayList<>();
		schemas.add(serviceName);
		if(dbHandler.serviceExists(serviceName)) {
			dbHandler.cleanupDb(schemas);
			Utils.removeServiceFiles(serviceName);
			dbHandler.unregisterService(serviceName);
			sendJsonResponse(response, (new DeleteServiceResponse()), HttpServletResponse.SC_OK);
		}
		else {
			ErrorResponse err = new ErrorResponse("ServiceNotFound", "The specified service does not exist");
			sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
		}
	}
    
}
