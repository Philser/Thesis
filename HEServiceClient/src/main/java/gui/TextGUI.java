package gui;

import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import client.RequestException;
import client.ServiceClient;
import communication.ComputationResultResponse;
import communication.CreateRunResponse;
import communication.CreateServiceResponse;
import communication.DeleteServiceRequest;
import communication.ErrorResponse;
import communication.MissingVariablesResponse;
import communication.SaveValueResponse;
import data.ComputationServiceItem;
import data.ServiceRunItem;
import homomorphic.HELib_Interface;

public class TextGUI {
	
	ServiceClient client;
	Scanner inputReader;
	
	public TextGUI(ServiceClient client, Scanner scanner) {
		this.client = client;
		inputReader = scanner;
	}
	
	public void interact() {
		String clearScreenSequence = "\033[2J\033[1;1H";
		while(true) {
			System.out.flush();
			printUI();
			String input = getUserInput();
			System.out.print(clearScreenSequence);
			System.out.flush();  
			switch(input) {
			case "1":
				handleSetTargetService();
				break;
			case "2":
				handleCreateService();
				break;
			case "3":
				if(clientHasService())
					handleSetTargetRun();
				break;
			case "4":
				if(clientHasService())
					handleCreateRun();
				break;
			case "5":
				if(clientHasRun())
					handleUploadValue();
				break;
			case "6":
				if(clientHasRun())
					handleFetchResult();
				break;
			case "7":
				handleDeleteService();
				break;
			case "q":
				return;
			default:
				break;
			}
		}
	}

	private void handleSetTargetService() {
		try {
			List<ComputationServiceItem> services = client.getServices();
			printAvailableServices(services);
			if(services.size() > 0) {
				ComputationServiceItem targetService = promptTargetService(services);
				client.setServiceName(targetService.getName());
				client.setNeedsBootstrapping(targetService.needsBootstrapping());
				if(client.hasRun()) //unset run in case it was set so the user has to choose a run again
					client.unsetRun();
			}
		} catch (ActionAbortedException e) {
			// Do nothing
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleSetTargetRun() {
		try {
			if(client.hasService()) {
				List<ServiceRunItem> runs = client.getRuns();
				printAvailableRuns(runs);
				if(runs.size() > 0) {
					String targetRun = promptTargetRun(runs);
					client.setRunId(targetRun);
				}
			}
			else {
				System.out.println("Please select a service first");
			}
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleCreateRun() {		
		try {		
			String peerGroupId = promptString("Create run for peer group: ");
			CreateRunResponse response = client.createRun(peerGroupId);
			client.setRunId(response.getRunId());
			System.out.println("Run created: " + response.getRunId());
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleUploadValue() {
		try {
			if(client.hasRun()) {
				String varName = promptString("Variable name:");
				String varValue = promptString("Variable value:");
				
				HELib_Interface heInterface = new HELib_Interface();
				System.out.println("Encrypting value. This may take a while...");
				String encryptedVarValue = heInterface.encryptPlain(varValue, client.needsBootstrapping());
				int fileSizeInKb = encryptedVarValue.getBytes().length / 1024;
				System.out.println("Done. Uploading value: " + fileSizeInKb + "kb");
				SaveValueResponse resp = client.uploadValue(client.getPeer_id(), varName, encryptedVarValue);
				System.out.println(resp.getMessage());
			} else {
				System.out.println("Please provide a run first.");
			}
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleFetchResult() {
		try {
			String responsePayload = client.getResult();
			Gson g = new Gson();
			ComputationResultResponse resp = g.fromJson(responsePayload, ComputationResultResponse.class);
			if(resp.status == null) { 
				// Could not parse to ComputationResultResponse
				// This means the server sent a MissingVariablesresponse
				MissingVariablesResponse mvResp = g.fromJson(responsePayload, MissingVariablesResponse.class);
				handleMissingVarsResponse(mvResp);
			}
			else {
				handleComputationResultResponse(resp);
			}
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMissingVarsResponse(MissingVariablesResponse mvResp) {
		String[] missingVarNames = mvResp.getMissingVariables();
		System.out.println("Cannot start run. The following variables are missing values: ");
		for(int i = 0; i < missingVarNames.length; i++) {
			System.out.println(missingVarNames[i]);
		}
		
	}

	private void handleComputationResultResponse(ComputationResultResponse compResult) {
		if(compResult.status.equals("Started")) {
			System.out.println("Computation started. "
					+ "Please wait while the service computes and try to fetch the result again later");
		}
		else if(compResult.status.equals("In Progress")) {
			System.out.println("Computation not done yet."
					+ "Please try again after a while.");
		}
		else if(compResult.status.equals("Done")) {
			System.out.println("Computation done. Got result. Decrypting...");
			HELib_Interface heInterface = new HELib_Interface();
			String result = heInterface.decryptCtxt(compResult.getResult(), client.needsBootstrapping());
			System.out.println("Result: " + result);
		}
	}

	private void handleCreateService() {
		try {
			String serviceName = promptString("Service name: ");
			String function = promptString("Function: ");
			CreateServiceResponse resp = client.createService(serviceName, function);
			client.setServiceName(serviceName);
			client.setNeedsBootstrapping(resp.needsBootstrapping);
			System.out.println("Service " + serviceName + " created:\n" + resp.getUrl());
			System.out.println("\nEstimated service runtime: " + resp.getEstimatedRuntime() + "s\n");
			System.out.println("Service generation takes approx. four minutes to complete.");
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			// The server sent an error response
			String error = parseErrorResponse(e);
			System.out.println(error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parseErrorResponse(RequestException e) {
		try {
			Gson g = new Gson();
			ErrorResponse err = g.fromJson(e.getResponseMessage(), ErrorResponse.class);
			return err.getErrorMessage();
		} catch(JsonSyntaxException jse) {
			return e.getResponseMessage();
		}
	}

	private void printAvailableServices(List<ComputationServiceItem> services) {
		if(services.size() == 0) {
			System.out.println("No services available");
		}
		else {
			for(ComputationServiceItem service: services) {
				String isReady = service.isReady() ? "ready" : "not ready";
				System.out.println(service.getName() + ": " 
						+ service.getFunction() 
						+ " (" + isReady + ")"
						+ "\t Estimated runtime: " + service.getEstimatedRuntime());
			}
		}
		System.out.println(""); //Styling
	}
	
	private ComputationServiceItem promptTargetService(List<ComputationServiceItem> services) throws ActionAbortedException {
		String givenServiceName = "";
		// Ask the user to input a service
		// Continues until a valid service name is given
		while(true) {
			givenServiceName = promptString("Abort:q\nChoose your service:");
			if(givenServiceName.equals("q")) {
				throw new ActionAbortedException();
			} else {
				for(ComputationServiceItem service: services) {
					if(service.getName().equals(givenServiceName)) { 
						return service;
					}
				}
			}
			System.out.print("Invalid service. ");
		}
	}
	
	private String promptTargetRun(List<ServiceRunItem> runs) {
		boolean runIsValid = false;
		String givenID = "";
		
		// Ask the user to input a run
		// Continues until a valid run id is given
		while(!runIsValid) {
			givenID = promptString("Choose your run:");
			for(ServiceRunItem run: runs) {
				if(run.getRunId().equals(givenID)) 
					runIsValid = true ;
			}
		}		
		return givenID;
	}
	
	private void printAvailableRuns(List<ServiceRunItem> runs) {
		if(runs.size() == 0) {
			System.out.println("No runs available. Please create one");
		}
		else {
			for(ServiceRunItem run: runs) {
				System.out.println("Run id: " + run.getRunId() + "\tPeer group: " + run.getPeerGroupId());
			}
		}
	}
	
	private String promptString(String outputMessage) {
		System.out.println(outputMessage);
		String input = "";
		if(inputReader.hasNextLine())
			input = inputReader.nextLine();
		return input;
	}
	
	private boolean clientHasService() {
		try {
			if(!client.hasService()) {
				System.out.println("No service provided. Please provide a service first");
				return false;
			} else if(!client.serviceIsReady()) {
				System.out.println("Service is not ready yet. Please wait for the deployment to finish.");
				return false;
			}
			else 
				return true;
		} catch (JsonSyntaxException e) {
			System.out.println("Error parsing service response: ");
			e.printStackTrace();
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean clientHasRun() {
		if(clientHasService()) {
			if(!client.hasRun()) {
				System.out.println("No run provided. Please provide a run first");
				return false;
			}
			return true;
		}
		return false;
	}



	private void printUI() {
		System.out.println("************ SAP HE Service Client ************");
		System.out.println("Service location: " + client.getServiceBaseUrl());
		if(client.hasRun())
			System.out.println("Selected run: " + client.getServiceName() + "/" + client.getRunId());
		else if(client.hasService())
			System.out.println("Selected service: " + client.getServiceName());
		System.out.println("Peer ID: " + client.getPeer_id());
		System.out.println("                      ");
		System.out.println("(1) Set target service");
		System.out.println("(2) Create computation service");
		System.out.println("(3) Choose run");
		System.out.println("(4) Create run");
		System.out.println("(5) Insert value");
		System.out.println("(6) Get result");
		System.out.println("(7) Delete service");
		System.out.println("(q) Exit");
		System.out.println("***********************************************");
		
	}
	
	private String getUserInput() {
		System.out.println("Choose an option: ");
		String input = inputReader.nextLine(); // Scans the next token of the input as an int.
		
		return input;
	}
	
	private void handleDeleteService() {
		try {
			String serviceName = promptString("Service name: ");
			client.deleteService(serviceName);
			System.out.println("Successfully deleted " + serviceName);
		} catch (RequestException e) {
			System.out.println(e.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class ActionAbortedException extends Exception {
		public ActionAbortedException() {
			super();
		}
	}

}
