package communication;

/**
 * Response message for a client requesting to create a new service
 * @author Philip Kaiser
 *
 */
public class CreateServiceResponse{
	
	final public String status = "creating";
	
	public String url;
	
	public double estimatedRuntime;
	
	public boolean needsBootstrapping;

	/**
	 * Response message for a client requesting to create a new service
	 * @param url URL of the newly created service
	 * @param estimatedRuntime Estimated runtime for a computation run with this service
	 * @param needsBootstrapping Indicates, whether the service needs to use bootstrapping.
	 */
	public CreateServiceResponse(String url, double estimatedRuntime, boolean needsBootstrapping) {
		this.url = url;
		this.estimatedRuntime = estimatedRuntime;
		this.needsBootstrapping = needsBootstrapping;
	}

	public String getStatus() {
		return status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public double getEstimatedRuntime() {
		return estimatedRuntime;
	}

	public void setEstimatedRuntime(double estimatedRuntime) {
		this.estimatedRuntime = estimatedRuntime;
	}

	public boolean isNeedsBootstrapping() {
		return needsBootstrapping;
	}

	public void setNeedsBootstrapping(boolean needsBootstrapping) {
		this.needsBootstrapping = needsBootstrapping;
	}


}
