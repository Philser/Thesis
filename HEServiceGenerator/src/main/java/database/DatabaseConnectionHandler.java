package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import misc.Constants;

/**
 * Central class for handling all database interaction. Provides various convenience methods.
 * 
 * @author D072531 - Philip Kaiser
 */
public class DatabaseConnectionHandler {
		
	//Database URI and credentials
	private String db_url = "jdbc:postgresql://localhost:5432/test";
	private String user = "testuser";
	private String password = "testuser";
	private String schema = "service_generator";

	
	private static final int MAX_TRIES = 1;
	
	private boolean debugActive = false;
	
	/**
	 * Central class for handling all database interaction. Provides various convenience methods.
	 * @param dbUrl URL of target database
	 * @param user User
	 * @param password Password
	 * @param schema Target schema
	 * @param debugActive If true, SQL queries will be written to System.out.
	 */
	public DatabaseConnectionHandler(String dbUrl, String user, String password, String schema, boolean debugActive) {
		this.debugActive = debugActive;
		this.db_url = dbUrl;
		this.user = user;
		this.password = password;
		this.schema = schema;
	}
	
	/**
	 * Opens a connection to the DB using JDBC-
	 * @return The active connection.
	 * @throws SQLException If the connection could not be established
	 * @throws ClassNotFoundException If the JDBC driver was not found
	 */
	private Connection openConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		
		int tryCounter = 0;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = DriverManager.getConnection(db_url, user, password);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to connect to database. Reason: " + e.getMessage());
				else
					tryCounter++;
			}
		}
		
		return connection;
	}
	
	/**
	 * Closes a connection to the DB.
	 * @param connection The connection to be closed.
	 * @throws SQLException If there was an error closing the connection.
	 */
	private void closeConnection(Connection connection) throws SQLException {
		if(connection == null)
			return;
		
		int tryCounter = 0;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection.close();
				break;
			} catch (SQLException e) {
				//Auto-generated catch block
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to close database connection.");
				else
					tryCounter++;
			}
		}
	}
	
	/**
	 * Creates all tables a service deployment needs.
	 * @param schemaName Name of the target schema. Usually the service name.
	 * @param function Arithmetic function the service is ought to evaluate.
	 * @param estimatedRuntime Estimated runtime for the service.
	 * @param needsBootstrapping Whether the service uses bootstrapping.
	 */
	public void createServiceTables(String schemaName, String function, String estimatedRuntime, boolean needsBootstrapping) {
		try {
			createSchema(schemaName);
			createRunTable(schemaName);
			createRunStatusTable(schemaName);
			createRunValueTable(schemaName);
			createRunResultTable(schemaName);
			registerService(schemaName, function, estimatedRuntime, needsBootstrapping);
		}
		catch(SQLException e) {
			System.out.println("Error creating tables: " + e.getMessage());
			System.out.println("Dropping schema " + schemaName);
			try {
				dropSchema(schemaName);
			} catch (SQLException de) {
				System.out.println("Error dropping schema " + schemaName + ": " + de.getMessage());
			}
		}
	}
	
	/**
	 * Cleans all tables for a given list of schema names.
	 * @param schemaNames List of schema names.
	 */
	public void cleanupDb(List<String> schemaNames) {
		
		try {
			for(String schema: schemaNames) {
				dropSchema(schema);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Drops a schema.
	 * @param schemaName Name of the schema.
	 * @throws SQLException If something went wrong while attempting to drop the schema.
	 */
	private void dropSchema(String schemaName) throws SQLException {
		String query = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE;";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
	}
	
	/**
	 * Creates a schema.
	 * @param schemaName Name of the schema.
	 * @throws SQLException If something went wrong while attempting to create the schema.
	 */
	private void createSchema(String schemaName) throws SQLException {
		String query = "CREATE SCHEMA IF NOT EXISTS " + schemaName + ";";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
	}
	
	/**
	 * Creates the table holding result ciphertexts for all runs.
	 * @param schemaName Name of the target schema.
	 * @throws SQLException
	 */
	private void createRunResultTable(String schemaName) throws SQLException {
		createTable(schemaName, "run_result", "run_id int REFERENCES " + schemaName + ".run(run_id), result text");		
	}

	/**
	 * Creates the table for the service generator mapping peers to peer groups.
	 * @throws SQLException
	 */
	private void createPeerGroupPeerTable() throws SQLException {
		createTable(this.schema, "peer_group_peer", "peer_group_id int REFERENCES " + this.schema + ".peer_group(peer_group_id), "
				+ "peer_id int REFERENCES " + this.schema + ".peer(peer_id)");
	}

	/**
	 * Creates the table holding the clients' uploaded ciphertext values.
	 * @param schemaName Name of the target schema.
	 * @throws SQLException
	 */
	private void createRunValueTable(String schemaName) throws SQLException {
		createTable(schemaName, "run_value", "run_id int REFERENCES " + schemaName + ".run(run_id), peer_id int REFERENCES " + this.schema + ".peer(peer_id),"
				+ " var_name VARCHAR(255), value text");		
	}

	/**
	 * Creates the table holding statuses for a service's runs.
	 * @param schemaName Name of the target schema.
	 * @throws SQLException
	 */
	private void createRunStatusTable(String schemaName) throws SQLException {
		createTable(schemaName, "run_status", "run_id int REFERENCES "  + schemaName + ".run(run_id), status int");
		
	}

	/**
	 * Creates the table holding information about all runs for a service.
	 * @param schemaName Name of the target schema.
	 * @throws SQLException
	 */
	private void createRunTable(String schemaName) throws SQLException {
		createTable(schemaName, "run", "run_id SERIAL PRIMARY KEY, peer_group_id INT REFERENCES "  + this.schema + ".peer_group(peer_group_id)");
	}

	/**
	 * Creates the table for the service generator holding peer groups information.
	 * @throws SQLException
	 */
	private void createPeerGroupTable() throws SQLException {
		createTable(this.schema, "peer_group", "peer_group_id SERIAL PRIMARY KEY");
	}
	
	/**
	 * Creates the table for the service generator holding peers information.
	 * @throws SQLException
	 */
	private void createPeerTable() throws SQLException {
		createTable(this.schema, "peer", "peer_id SERIAL PRIMARY KEY");
	}
	
	/**
	 * Checks, whether the service is registered with the central service generator's service table.
	 * @param serviceName Name of the service
	 * @return True, if the service is registered with the central service generator's service table.
	 * @throws SQLException
	 */
	public boolean serviceExists(String serviceName) throws SQLException {
		String query = "SELECT EXISTS(SELECT service_name FROM " + schema + ".service where service_name = '" + serviceName + "');";
		
		int tryCounter = 0;
		Connection connection = null;
		boolean exists = false;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				ResultSet result = execQuery(query, statement);
				if(result.next())
					exists = result.getBoolean(1);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
		return exists;
	}
	
	/**
	 * Creates a new table.
	 * @param schema Target schema.
	 * @param table Target table name.
	 * @param definitions SQL definition of the table.
	 * @throws SQLException
	 */
	private void createTable(String schema, String table, String definitions) throws SQLException {
		String query = "CREATE TABLE " + schema + "." + table + "(" + definitions + ");";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
	}	
	
	/**
	 * Execute a query without expecting a return value.
	 * @param query Query to be executed
	 * @param statement Prepared {@link Statement}
	 * @throws SQLException
	 */
	private void execUpdate(String query, Statement statement) throws SQLException {
		if(debugActive)
			System.out.println("Executing query: " + query);
		statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
	}
	
	/**
	 * Execute a query while expecting a return value.
	 * @param query Query to be executed
	 * @param statement Prepared {@link Statement}
	 * @throws SQLException
	 */
	private ResultSet execQuery(String query, Statement statement) throws SQLException {
		if(debugActive)
			System.out.println("Executing query: " + query);
		ResultSet result = statement.executeQuery(query);
		return result;
	}
	
	/**
	 * Initiates the central service generator's tables if they are not present.
	 */
	public void initServiceGeneratorTables() {
		try {
			createSchema(schema);
			createServiceInfoTable();
			createPeerTable();
			createPeerGroupTable();
			createPeerGroupPeerTable();
			insertTestData();
		}
		catch(SQLException e) {
			System.out.println("Error creating service generator tables: " + e.getMessage());
			System.out.println("Dropping schema " + schema);
			try {
				dropSchema(schema);
			} catch (SQLException de) {
				System.out.println("Error dropping schema " + schema + ": " + de.getMessage());
			}
		}
	}

	/**
	 * Inserts test data into the central service generator's tables.
	 * @throws SQLException
	 */
	private void insertTestData() throws SQLException {
		String peerGroupData = "INSERT INTO " + this.schema + ".peer_group VALUES (1), (2), (3);";
		String peerData = "INSERT INTO " + this.schema + ".peer VALUES (1), (2), (3), (4), (5), (6), (7), (8);";
		String peersInGroupData = "INSERT INTO " + this.schema + ".peer_group_peer VALUES "
				+ "(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),"
				+ "(2, 1), (2, 2), (2, 3), (2, 4),"
				+ "(3, 5), (2, 6), (2, 7), (2, 8);";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(peerGroupData, statement);
				execUpdate(peerData, statement);
				execUpdate(peersInGroupData, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query. Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
	}

	/**
	 * Creates the central service generator's table that holds information about all available services. 
	 * If a service does not appear in this table, it is not considered available.
	 * @throws SQLException
	 */
	private void createServiceInfoTable() throws SQLException {
		createTable(schema, Constants.CENTRAL_SERVICE_TABLE_NAME, "service_name varchar(255) PRIMARY KEY, function text, is_ready boolean, "
				+ "estimated_runtime varchar(255), needs_bootstrapping boolean");
	}

	/**
	 * Inserts a service's information into the central service generator's services table and thus make it available.
	 * @param serviceName Name of the service
	 * @param function Arithmetic function the service evaluates
	 * @param estimatedRuntime Estimated runtime for a run of the service
	 * @param needsBootstrapping Whether the service needs to bootstrap while evaluating
	 * @throws SQLException
	 */
	public void registerService(String serviceName, String function, String estimatedRuntime, boolean needsBootstrapping) throws SQLException {
		// Insert vars into central service table
		String query = "INSERT INTO " + schema + ".service VALUES('" + serviceName + "', '" 
				+ function + "', FALSE, '" + estimatedRuntime + "', " + needsBootstrapping + ");";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}	
	}
	
	/**
	 * Unregisters the service.
	 * @param serviceName Name of the service.
	 * @throws SQLException
	 */
	public void unregisterService(String serviceName) throws SQLException {
		// Insert vars into central service table
		String query = "DELETE FROM " + schema + ".service WHERE service_name ='" + serviceName + "';";
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				execUpdate(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}	
	}

	/**
	 * Checks whether the service generator's service table is present. 
	 * If not, the service generator's schema is considered uninitialized.
	 * @return Whether or not the service generator's schema is initialized.
	 */
	public boolean serviceGeneratorIsInitialized() {
		String query = "SELECT EXISTS(SELECT 1 FROM pg_tables WHERE schemaname='" + schema + "' and tablename='service');";
		
		int tryCounter = 0;
		Connection connection = null;
		boolean exists = false;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				ResultSet result = execQuery(query, statement);
				if(result.next())
					exists = result.getBoolean(1);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) {
					System.out.println("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
					break;
				}
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				System.out.println("Not able to execute sql query: PGSQL Driver not found");
				break;
			}
			finally {
				try {
					closeConnection(connection);
				}
				catch (SQLException e) {
					System.out.println("Could not close connection: " + e.getMessage());
				}
			}
		}
		return exists;
	}

	/**
	 * Queries all available services.
	 * @return Set of available services.
	 */
	public ResultSet getServices() {
		String query = "SELECT service_name, function, is_ready, estimated_runtime, needs_bootstrapping FROM " + schema + "." 
	+ Constants.CENTRAL_SERVICE_TABLE_NAME + ";";
		
		int tryCounter = 0;
		Connection connection = null;
		ResultSet result = null;
		
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				result = execQuery(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) {
					System.out.println("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
					break;
				}
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				System.out.println("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				try {
					closeConnection(connection);
				}
				catch (SQLException e) {
					System.out.println("Could not close connection: " + e.getMessage());
				}
			}
		}
		return result;
	}
	
	/**
	 * Sets the ready state of a service.
	 * @param serviceName Name of the service.
	 * @param isReady Status to set.
	 * @throws SQLException
	 */
	public void setIsReadyForService(String serviceName, boolean isReady) throws SQLException {
		String isReadyString = isReady ? "TRUE" : "FALSE";
		String query = "UPDATE " + this.schema + ".service SET is_ready=" + isReadyString + " WHERE service_name = '" + serviceName + "';";
		queryTable(query, true);
	}
	
	/**
	 * Queries the state of a service.
	 * @param serviceName Name of the service.
	 * @return True, if service is ready.
	 * @throws SQLException
	 */
	public boolean getServiceIsReady(String serviceName) throws SQLException {
		String query = "SELECT is_ready FROM " + this.schema + ".service WHERE service_name = '" + serviceName + "';";
		
		ResultSet result = queryTable(query, false);
		if(result.next())
			return result.getBoolean(1);
		else
			throw new SQLException("Error fetching result for service " + serviceName);
		
	}
	
	/**
	 * Queries a table.
	 * @param query SQL query
	 * @param insert If true, no result is returned.
	 * @return Result set of insert is set to false.
	 * @throws SQLException
	 */
	private ResultSet queryTable(String query, boolean insert) throws SQLException {
		ResultSet queryResult = null;		
		
		int tryCounter = 0;
		Connection connection = null;
		while(tryCounter <= MAX_TRIES) {
			try {
				connection = openConnection();
				Statement statement = connection.createStatement();
				if(insert)
					execUpdate(query, statement);
				else
					queryResult = execQuery(query, statement);
				break;
			} catch (SQLException e) {
				if (tryCounter == MAX_TRIES) 
					throw new SQLException("Not able to execute sql query: " + query + ". Reason: " + e.getMessage());
				else 
					tryCounter++;
			} catch(ClassNotFoundException cnfe) {
				throw new SQLException("Not able to execute sql query: PGSQL Driver not found");
			}
			finally {
				closeConnection(connection);
			}
		}
		
		return queryResult;
	}

}

