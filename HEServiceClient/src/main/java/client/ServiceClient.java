package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import communication.AvailableRunsResponse;
import communication.AvailableServicesResponse;
import communication.ComputationResultResponse;
import communication.CreateRunRequest;
import communication.CreateRunResponse;
import communication.CreateServiceRequest;
import communication.CreateServiceResponse;
import communication.DeleteServiceRequest;
import communication.MissingVariablesResponse;
import communication.SaveValueRequest;
import communication.SaveValueResponse;
import data.ComputationServiceItem;
import data.ServiceRunItem;

/**
 * 
 * @author D072531 - Philip Kaiser
 * @brief Class capable of communicating with the HE cloud service(s).
 */
public class ServiceClient {
	
	private String serviceBaseUrl;
	
	private final String centralEndpoint = "serviceGenerator";
	
	private String peerId;
	
	private String serviceName;
	private boolean hasService = false;
	
	private String runId;
	private boolean hasRun = false;
	
	// Need to know if we need to use the bootstrappable context for en-/decrypting
	private boolean needsBootstrapping = false; 
	
	public ServiceClient(String peerId, String serviceBaseUrl) {
		if(!serviceBaseUrl.endsWith("/"))
			serviceBaseUrl += "/";
		this.serviceBaseUrl = serviceBaseUrl;
		this.peerId = peerId;
	}
	
	/**
	 * 
	 * @return List of {@link ServiceRunItem}
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Get all available Runs for the service the {@link ServiceClient} is currently set to.
	 */
	public List<ServiceRunItem> getRuns() throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getServiceName() + "/";
		String responsePayload = sendGet(targetUrl);
		Gson g = new Gson();
		AvailableRunsResponse response = g.fromJson(responsePayload, AvailableRunsResponse.class);
		//System.out.println("Response: " + response.
		return response.getRuns();
	}
	
	/**
	 * 
	 * @return List of {@link ComputationServiceItem}
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Get all available computation services.
	 */
	public List<ComputationServiceItem> getServices() throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getCentralEndpoint();
		String responsePayload = sendGet(targetUrl);
		Gson g = new Gson();
		AvailableServicesResponse response = g.fromJson(responsePayload, AvailableServicesResponse.class);
		//System.out.println("Response: " + response.
		return response.getServices();
	}
	
	/**
	 * 
	 * @param peerGroupId The ID of the peer group that wants to perform a computation on the service.
	 * @return The {@link CreateRunResponse} containing information about the created run.
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Creates a run of a service for a peer group.
	 */
	public CreateRunResponse createRun(String peerGroupId) throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getServiceName() + "/createRun/";
		CreateRunRequest reqObj = new CreateRunRequest(peerGroupId);
		Gson g = new Gson();
		String requestPayload = g.toJson(reqObj);
		String responsePayload = sendPost(targetUrl, requestPayload);
		CreateRunResponse resp = g.fromJson(responsePayload, CreateRunResponse.class);
		
		return resp;
	}
	
	/**
	 * 
	 * @param serviceName Name of the service to be created
	 * @param function Target function
	 * @return {@link CreateServiceResponse} containing information about the created service.
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Create a service.
	 */
	public CreateServiceResponse createService(String serviceName, String function) throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getCentralEndpoint() + "/";
		CreateServiceRequest reqObj = new CreateServiceRequest(function, serviceName);
		Gson g = new Gson();
		String requestPayload = g.toJson(reqObj);
		
		String responsePayload = sendPost(targetUrl, requestPayload);
		CreateServiceResponse resp = g.fromJson(responsePayload, CreateServiceResponse.class);
		
		return resp;
	}
	
	/**
	 * 
	 * @param peerId ID of the peer uploading the value
	 * @param varName Name of the variable the value is to be provided for.
	 * @param encryptedVarValue Encrypted value of the variable.
	 * @return {@link SaveValueResponse}
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Uploads a ciphertext to the target service and run
	 */
	public SaveValueResponse uploadValue(String peerId, String varName, String encryptedVarValue) 
			throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getServiceName() + "/" + this.getRunId();
		
		SaveValueRequest reqObj = new SaveValueRequest(peerId, varName, encryptedVarValue);
		Gson g = new Gson();
		String requestPayload = g.toJson(reqObj);
		
		String responsePayload = sendPost(targetUrl, requestPayload);
		SaveValueResponse resp = g.fromJson(responsePayload, SaveValueResponse.class);
		
		return resp;
	}
	
	/**
	 * 
	 * @return {@link ComputationResultResponse} given by the service
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws JsonSyntaxException if the response could not be parsed to JSON
	 * @throws Exception
	 * 
	 * Requests the result and receives a response that depends on whether the result has been computed yet or not.
	 */
	public String getResult() throws RequestException, JsonSyntaxException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getServiceName() + "/" + this.getRunId();
		String responsePayload = sendGet(targetUrl);
		
		return responsePayload;
	}
	
	public boolean serviceIsReady() throws JsonSyntaxException, RequestException, Exception {
		List<ComputationServiceItem> services = getServices();
		String serviceToCheck = this.getServiceName();
		for(ComputationServiceItem service: services) {
			if(service.getName().equals(serviceToCheck))
				return service.isReady();
		}
		return false;
	}

	
	/**
	 * @param url Target URL
	 * @return The message payload
	 * @throws Exception
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * 
	 * Sends a HTTP GET request to the target.
	 */
	private String sendGet(String url) throws RequestException, Exception {
		
		URL urlObj = new URL(url);
		
		HttpURLConnection connection = null;
		
		connection = (HttpURLConnection) urlObj.openConnection();	
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		connection.setDoOutput(true);
		
		int statusCode = connection.getResponseCode();
		 
		InputStream in;
		if(statusCode > 299) {
			in = connection.getErrorStream();
			String responsePayload = readFromInputStream(in);
			throw new RequestException(statusCode, responsePayload);
		}
		else {
			in = connection.getInputStream();
			String responsePayload = readFromInputStream(in);
			return responsePayload;
		}
	}
	
	/**
	 * 
	 * @param url Target URL
	 * @param requestPayload POST body
	 * @return The response body
	 * @throws RequestException if the server returns an HTTP error code (>299)
	 * @throws Exception
	 */
	private String sendPost(String url, String requestPayload) throws RequestException, Exception {
		
		URL urlObj = new URL(url);
		
		HttpURLConnection connection = null;
		
		connection = (HttpURLConnection) urlObj.openConnection();	
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.writeBytes(requestPayload);
		outputStream.flush();
		outputStream.close();

		int statusCode = connection.getResponseCode();
		 
		InputStream in;
		if(statusCode > 299) {
			in = connection.getErrorStream();
			String responsePayload = readFromInputStream(in);
			throw new RequestException(statusCode, responsePayload);
		}
		else {
			in = connection.getInputStream();
			String responsePayload = readFromInputStream(in);
			return responsePayload;
		}
	}
	
	
	/**
	 * @param in {@link InputStream} to read from
	 * @return The error message or an empty string, if the {@link InputStream} did not contain data
	 * @throws IOException
	 * 
	 * Reads from an {@link InputStream} and returns the data as a String.
	 */
	private String readFromInputStream(InputStream in) throws IOException {
		if(in != null) { //Response has a body
			BufferedReader bin = new BufferedReader(new InputStreamReader(in)); 
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = bin.readLine()) != null) {
				response.append(inputLine);
			}
			
			in.close();
			return response.toString();
		}
		else {
			return "";
		}
	}
	
	public String deleteService(String serviceName) throws RequestException, Exception {
		String targetUrl = this.getServiceBaseUrl() + this.getCentralEndpoint() + "/deleteService";
		DeleteServiceRequest delReq = new DeleteServiceRequest(serviceName);
		Gson g = new Gson();
		String requestPayload = g.toJson(delReq);
		return sendPost(targetUrl, requestPayload);
	}

	
	public String getServiceBaseUrl() {
		return serviceBaseUrl;
	}

	public void setServiceBaseUrl(String serviceBaseUrl) {
		this.serviceBaseUrl = serviceBaseUrl;
	}

	public String getCentralEndpoint() {
		return centralEndpoint;
	}

	public String getPeer_id() {
		return peerId;
	}

	public void setPeerId(String peer_id) {
		this.peerId = peer_id;
	}

	public String getServiceName() {
			return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
		this.hasService = true;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String run_id) {
		this.runId = run_id;
		this.hasRun = true;
	}
	
	public void unsetRun() {
		this.runId = "";
		this.hasRun = false;
	}
	
	public boolean hasRun() {
		return hasRun;
	}
	
	public boolean hasService() {
		return hasService;
	}
	
	public void setNeedsBootstrapping(boolean needsBootstrapping) {
		this.needsBootstrapping = needsBootstrapping;
	}
	
	public boolean needsBootstrapping() {
		return this.needsBootstrapping;
	}

}
