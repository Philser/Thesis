package thesis.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.DatabaseConnectionHandler;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import communication.AvailableRunsResponse;
import communication.ComputationResultResponse;
import communication.CreateRunRequest;
import communication.CreateRunResponse;
import communication.ErrorResponse;
import communication.GetPeerGroupsForPeerResponse;
import communication.MissingVariablesResponse;
import communication.SaveValueRequest;
import communication.SaveValueResponse;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns={"/"}, asyncSupported=true)
public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private List<String> variablesList = new ArrayList<String>();
	DatabaseConnectionHandler dbConnectionHandler = new DatabaseConnectionHandler("test", true);
	private String KEY_SERVICE_LOCATION = "";

	@Override
	public void init() throws ServletException {
		variablesList.add("x1");
		variablesList.add("x2");
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// Accept variables
		// Register clients
    	try {
    		handlePost(request, response);
    	}
    	catch(Exception e) {
    		ErrorResponse er = new ErrorResponse("Internal error", "Something went wrong");
    		e.printStackTrace();
    		sendJsonResponse(response, er, 500);
    	}
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
      throws ServletException, IOException
    {
    	try {
    		handleGet(request, response);
    	}
		catch(Exception e) {
			ErrorResponse er = new ErrorResponse("Internal error", "Something went wrong");
    		e.printStackTrace();
    		sendJsonResponse(response, er, 500);
		}	
    }
    
    private void sendJsonResponse(HttpServletResponse response, Object responseObject, int httpStatus) throws IOException {
    	Gson g = new Gson();
        String jsonResponse = g.toJson(responseObject);
        response.setContentType("application/json");
		response.setStatus(httpStatus);
        response.getWriter().print(jsonResponse);
    }

	private void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		//TODO Move stuff into separate methods
		String runIdPattern = "[0-9]+";
		String[] segments = request.getRequestURI().split("/");
		String endpoint = segments[segments.length - 1];
		System.out.println("Endpoint: " + endpoint);
		
		if(endpoint.equalsIgnoreCase("test")) {
			// List of runs
			handleGetList(response);
		}
		else if(endpoint.equalsIgnoreCase("peergroups")) {
			handleGetPeerGroups(request, response);
		}
		else if (endpoint.matches(runIdPattern)) {
			// Get status (Missing Vars/Computing) or result
			String runId = endpoint;
			if(dbConnectionHandler.runExists(runId)) {
				if(allVariablesProvided(endpoint)) {
					handleComputation(endpoint, response);
			    } else {
			    	handleMissingVars(endpoint, response);
		    	}
			}
			else {
				ErrorResponse er = new ErrorResponse("invalidRunid", "The Run you provided does not exist");
				sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
			}		
		}		
	}

	private void handleGetPeerGroups(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		String peerId = request.getParameter("peerid");
		List<String> peerGroupIds = dbConnectionHandler.getPeerGroupsForPeer(peerId);
		GetPeerGroupsForPeerResponse respObj = new GetPeerGroupsForPeerResponse(peerGroupIds);
		sendJsonResponse(response, respObj, HttpServletResponse.SC_OK);
	}

	private void handleGetList(HttpServletResponse response) throws SQLException, IOException {
		Map<String, String> runs = new HashMap<>();
		ResultSet result = dbConnectionHandler.getRuns();
		while(result.next()) {
			runs.put(result.getString(1), result.getString(2)); //1 = run_id, 2 = peer_group_id
		}
		AvailableRunsResponse respObj = new AvailableRunsResponse(runs);
        sendJsonResponse(response, respObj, HttpServletResponse.SC_OK);
	}

	private void handleMissingVars(String runId, HttpServletResponse response) throws SQLException, IOException {
		List<String> missingVars = getMissingVariables(runId);
    	String[] valArr = new String[missingVars.size()];
    	valArr = missingVars.toArray(valArr);
    	MissingVariablesResponse mvResp = new MissingVariablesResponse(valArr);
    	sendJsonResponse(response, mvResp, HttpServletResponse.SC_OK);
	}

	private void handleComputation(String runId, HttpServletResponse response) throws SQLException, IOException {
		HEComputationObject heObj = new HEComputationObject(runId, dbConnectionHandler, this.KEY_SERVICE_LOCATION); 
		String computationStatus = "";
        if(!heObj.isDone() && !heObj.isComputing()) {
        	Map<String, String> varMap = getFunctionVariables(runId);
            heObj.setVariableValues(getVariableValues(varMap));
            	
            
            Thread t = new Thread(heObj);
            t.start();          
            computationStatus = "Started";
        }
        else {
        	if(heObj.isDone()) {
        		computationStatus = "Done";
        	}
        	else {
        		computationStatus = "In Progress";
        	}
        }
        ComputationResultResponse crResp = new ComputationResultResponse(heObj.getResult(), computationStatus);
        sendJsonResponse(response, crResp, HttpServletResponse.SC_OK);
		
	}

	private List<String> getMissingVariables(String runId) throws SQLException {
		Map<String, String> obtainedVars = getFunctionVariables(runId);
		List<String> missingVars = new ArrayList<>();
		for(String entry : variablesList) {
			if(!obtainedVars.containsKey(entry))
				missingVars.add(entry);
		}

		return missingVars;
	}

	private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		String createRunEndpoint = "createRun";
		String runIdPattern = "[0-9]+";
		String[] segments = request.getRequestURI().split("/");
		String endpoint = segments[segments.length - 1];
				
		System.out.println("URI: " + request.getRequestURI());
		System.out.println("Endpoint: " + endpoint);
		if(endpoint.equalsIgnoreCase(createRunEndpoint)) {
			handleCreateRun(request, response);
		}
		else if (endpoint.matches(runIdPattern)) {			
			//Insert variable
			System.out.println("URL matches Run ID: ");
			handlePostedVariable(request, response, endpoint);
		}
	}

	private void handleCreateRun(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
		try {
			CreateRunRequest cr = parseRequest(request, CreateRunRequest.class);
			String peerGroupId = cr.getPeerGroupId();
			if(!dbConnectionHandler.peerGroupExists(peerGroupId)) {
				ErrorResponse err = new ErrorResponse("invalidPeerGroup", "The peer group you provided does not exist.");
				sendJsonResponse(response, err, HttpServletResponse.SC_BAD_REQUEST);
			}
			// Create db entry
			String runId = dbConnectionHandler.createRun(peerGroupId);
			// Return run id
			CreateRunResponse rc = new CreateRunResponse(runId);
			sendJsonResponse(response, rc, HttpServletResponse.SC_OK);
		}
		catch(JsonParseException jpe) {
			ErrorResponse error = new ErrorResponse("Parsing error", "Invalid JSON request");
			sendJsonResponse(response, error, HttpServletResponse.SC_BAD_REQUEST);
		}		
	}

	private <T> T parseRequest(HttpServletRequest request, Class<T> classType) throws IOException {
		String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		Gson g = new Gson();
        T jsonResponse = g.fromJson(payload, classType);
		return jsonResponse;
	}

	private String[] getVariableValues(Map<String, String> varMap) {
		String[] strArr = new String[varMap.size()];
		int c = 0;
    	for(String str : varMap.values()) {
    		strArr[c] = str;
			c+=1;
		}
		return strArr;
	}

	private boolean allVariablesProvided(String runId) throws SQLException {
		int varsProvided = dbConnectionHandler.getVarCountForRun(runId);
		if(varsProvided == variablesList.size())
			return true;
		return false;
	}


	private void handlePostedVariable(HttpServletRequest request, HttpServletResponse response, 
			String runId) throws IOException, SQLException {
		SaveValueRequest req = parseRequest(request, SaveValueRequest.class);
		
		if(variablesList.contains(req.getVarName()) && req.getVarName() != null) {
			String peerId = req.getPeerId();
			if(dbConnectionHandler.runExists(runId)) {
				HEComputationObject heObj = new HEComputationObject(runId, dbConnectionHandler, this.KEY_SERVICE_LOCATION);
				if(heObj.isDone()) {//Value has already been computed
					ErrorResponse er = new ErrorResponse("AlreadyComputed", "The run does not accept any values because it is already finished");
					sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
				}
				else {
					if(isCtxtFormat(req.getVarValue())) {
						insertRunValue(runId, peerId, req.getVarName(), req.getVarValue(), response);
					}
					else {
						ErrorResponse er = new ErrorResponse("invalidValue", "The value you provided is not in the correct ciphertext format");
						sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
					}							
				}
			}
			else {
				ErrorResponse er = new ErrorResponse("invalidRunid", "The Run you provided does not exist");
				sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else {
			ErrorResponse er = new ErrorResponse("invalidVarName", "You provided an invalid variable name");
			sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
		}
	}

    private void insertRunValue(String runId, String peerId, String varName, String varValue, HttpServletResponse response) 
    		throws IOException, SQLException {
//    	if(dbConnectionHandler.runValueExists(runId, peerId, varName)) { 
//    		ErrorResponse er = new ErrorResponse("error", "ValueExists", "A value for the given variable already exists");
//    		sendJsonResponse(response, er, HttpServletResponse.SC_BAD_REQUEST);
//		}
//    	else {
    		//insert
    		dbConnectionHandler.insertRunValue(runId, peerId, varName, varValue);
    		SaveValueResponse vs = new SaveValueResponse("Value for variable " + varName + " saved");
    		sendJsonResponse(response, vs, HttpServletResponse.SC_OK);
//    	}
	}

	private boolean isCtxtFormat(String varValue) {
		if(varValue.startsWith("["))
			return true;
		return false;
	}

	private Map<String, String> getFunctionVariables(String runId) throws SQLException {
    	// Get all varnames and -values for a runId that have already been provided
    	ResultSet result = dbConnectionHandler.getClientValuesForRun(runId);
    	Map<String, String> varMap = new HashMap<>();
    	while(result.next()) {
    		varMap.put(result.getString(1), result.getString(2));
    	}
    	// Turn it into a Map<>
    	return varMap;
    }
}
