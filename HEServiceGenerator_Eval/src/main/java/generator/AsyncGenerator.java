package generator;

import java.sql.SQLException;

import database.DatabaseConnectionHandler;

/**
 * Master generator class. Triggers the generation of the service's HE code, servlet and database tables.
 * @author Philip Kaiser
 *
 */
public class AsyncGenerator implements Runnable {

	ServiceGenerator serviceGenerator;
	String buildFilesDirectory;
	DatabaseConnectionHandler dbHandler;
	
	/**
	 * Master generator class. Triggers the generation of the service's HE code, servlet and database tables.
	 * @param buildFilesDirectory Path to directory where the temporary build files are to be placed
	 * @param serviceGenerator Instance of {@link ServiceGenerator}
	 * @param dbHandler Instance of {@link DatabaseConnectionHandler}
	 */
	public AsyncGenerator(String buildFilesDirectory, ServiceGenerator serviceGenerator, DatabaseConnectionHandler dbHandler) {
		this.buildFilesDirectory = buildFilesDirectory;
		this.serviceGenerator = serviceGenerator;
		this.dbHandler = dbHandler;
	}

	@Override
	public void run() {
		try {
			serviceGenerator.generateService();
			//clientGenerator.generateClient();
			//Utils.cleanup(buildFilesDirectory);
			dbHandler.setIsReadyForService(serviceGenerator.serviceName, true);
		}
		catch (SQLException sqle) {
			System.out.println("Error setting service status: " + sqle.getMessage());
		}
	}
	
	public boolean isDone(String serviceName) throws SQLException {		
		return dbHandler.getServiceIsReady(serviceName);
	}
}
