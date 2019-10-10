package communication;

/**
 *  Request by a client to delete a service
 * @author Philip Kaiser
 *
 */
public class DeleteServiceRequest {
	
	private String serviceName;

	/**
	 * Request by a client to delete a service
	 * @param serviceName Name of the service to be deleted.
	 */
	public DeleteServiceRequest(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
