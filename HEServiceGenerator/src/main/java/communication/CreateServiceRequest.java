package communication;

/**
 * Request message coming from a client who aims to create a new computation service.  
 * @author Philip Kaiser
 *
 */
public class CreateServiceRequest {
	
	private String function;
	
	private String serviceName;

	/**
	 * Request message coming from a client who aims to create a new computation service.  
	 * @param function Arithmetic function of the service
	 * @param serviceName Name of the service
	 */
	public CreateServiceRequest(String function, String serviceName) {
		this.function = function;
		this.serviceName = serviceName;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
