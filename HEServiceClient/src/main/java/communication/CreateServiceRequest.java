package communication;

public class CreateServiceRequest {
	
	private String function;
	
	private String serviceName;

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
