package misc;

/**
 * Holds configuration information for the generator classes.
 * @author Philip Kaiser
 *
 */
public interface Config {
	
	
	public String getDbUrl();
	
	public String getUser();
	
	public String getPassword();
	
	public String getSchema();
	
	public String getTomcatPath();
	
	public String getKeyServiceURL();
}
