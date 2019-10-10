package communication;

import java.util.List;

import data.ComputationServiceItem;

public class AvailableServicesResponse {
	
	private List<ComputationServiceItem> services;
	
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
