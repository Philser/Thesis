package misc;

/**
 * Holds various credentials and configuration parameters.
 * @author Philip Kaiser
 *
 */
public class ProductiveConfig implements Config {

	@Override
	public String getDbUrl() {
		// TODO Auto-generated method stub
		return "jdbc:postgresql://10.11.241.102:33289/goDs5Z1EFqzuyKxL";
	}

	@Override
	public String getUser() {
		// TODO Auto-generated method stub
		return "wN3cHrdvt8916bHv";
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return "qvamXNnNBuSWrCJf";
	}

	@Override
	public String getSchema() {
		// TODO Auto-generated method stub
		return "service_generator";
	}
	
	@Override
	public String getTomcatPath() {
		// TODO Auto-generated method stub
		return "/usr/local/tomcat/webapps";
	}

	@Override
	public String getKeyServiceURL() {
		// TODO Auto-generated method stub
		return "https://keyservice.cfapps.sap.hana.ondemand.com/keyservice";
	}
}
