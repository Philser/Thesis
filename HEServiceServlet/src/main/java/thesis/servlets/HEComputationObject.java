package thesis.servlets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import database.DatabaseConnectionHandler;
import jni.HECodeInterface;

public class HEComputationObject implements Runnable {
	
	private static HECodeInterface heInterface = new HECodeInterface();
	private String[] variableValues = null;
	private DatabaseConnectionHandler dbConnHandler = null;
	private String runId = "";
	
	public HEComputationObject(String runId, DatabaseConnectionHandler dbConnHandler) {
		this.dbConnHandler = dbConnHandler;
		this.runId = runId;
	}
	
	@Override
	public void run() {
		// Set status to busy
		try {
			//TODO enum for status
			ResultSet resultSet = dbConnHandler.getStatusForRun(this.runId);
			int status = 0; // 0 == Waiting for values, 1 == Calculating, 2 == Done
			if(resultSet.next()) {
				status = resultSet.getInt(1);
				if(status == 1) {
					return; // Run is already computing
				}
			}
			dbConnHandler.setStatusForRun(this.runId, 1);
			//Fetch & Deploy crypto files
			boolean needs_bootstrapping = dbConnHandler.getIsBootstrappable();
			// Compute
			String result = heInterface.calculate(variableValues);
			// Save computation
			storeComputationResult(result);
			// Set status to done
			dbConnHandler.setStatusForRun(this.runId, 2);			
		} catch (SQLException e) {
			System.out.println("Error running query: " + e.getMessage());
		}
	}
	
	private void storeComputationResult(String result) throws SQLException {
		dbConnHandler.storeRunResult(this.runId, result);
	}

	public String getResult() {
		try {
			ResultSet resultSet = dbConnHandler.getRunResult(this.runId);
			if(resultSet.next())
				return resultSet.getString(1);
		} catch (SQLException e) {
			System.out.println("Error fetching status: " + e.getMessage());
		}
		
		return "";
	}
	
	public void setVariableValues(String[] variableValues) {
		this.variableValues = variableValues;
	}
	
	
	public boolean isDone() {
		try {
			ResultSet resultSet = dbConnHandler.getStatusForRun(this.runId);
			if(resultSet.next())
				return resultSet.getInt(1) == 2;
		} catch (SQLException e) {
			System.out.println("Error fetching status: " + e.getMessage());
		}
		
		return false;
	}
	
	public boolean isComputing() {
		try {
			ResultSet resultSet = dbConnHandler.getStatusForRun(this.runId);
			if(resultSet.next())
				return resultSet.getInt(1) == 1;
		} catch (SQLException e) {
			System.out.println("Error fetching status: " + e.getMessage());
		}
		
		return false;
	}

}
