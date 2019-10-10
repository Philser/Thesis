package communication;

import java.util.List;

import database.ComputationServiceItem;

/**
 * Response message for a client requesting a list of available services 
 * @author Philip Kaiser
 *
 */
public class AvailableServicesResponse {
	
	private List<ComputationServiceItem> services;
	
	/**
	 * Response message for a client requesting a list of available services 
	 * @param services List of {@link ComputationServiceItem} containing all available services
	 */
	public AvailableServicesResponse(List<ComputationServiceItem> services) {
		this.services = services;
	}

	public List<ComputationServiceItem> getServices() {
		return services;
	}

	public void setServices(List<ComputationServiceItem> services) {
		this.services = services;
	}
}
