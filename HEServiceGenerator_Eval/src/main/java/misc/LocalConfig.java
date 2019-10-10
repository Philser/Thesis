package misc;

public class LocalConfig implements Config {
	
	@Override
	public String getDbUrl() {
		// TODO Auto-generated method stub
		return "jdbc:postgresql://localhost:5432/test";
	}

	@Override
	public String getUser() {
		// TODO Auto-generated method stub
		return "testuser";
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return "testuser";
	}

	@Override
	public String getSchema() {
		// TODO Auto-generated method stub
		return "service_generator";
	}
	
	@Override
	public String getTomcatPath() {
		// TODO Auto-generated method stub
		return "/opt/tomcat/latest/webapps";
	}

	@Override
	public String getKeyServiceURL() {
		return "https://keyservice.cfapps.sap.hana.ondemand.com/keyservice";
	}

}
