package communication;

public class CreateServiceResponse{
	
	public String status;
	
	public String url;
	
	public double estimatedRuntime;
	
	
	public boolean needsBootstrapping;

	public CreateServiceResponse(String status, String url, double estimatedRuntime, boolean needsBootstrapping) {
		this.status = status;
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

	public boolean needsBootstrapping() {
		return needsBootstrapping;
	}

	public void setNeedsBootstrapping(boolean needsBootstrapping) {
		this.needsBootstrapping = needsBootstrapping;
	}


}
