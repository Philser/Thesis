package data;

/**
 * 
 * @author D072531 - Philip Kaiser
 * @brief Contains information about all computation services the cloud service is offering.
 */
public class ComputationServiceItem {
	
	private String name;
	
	private String function;
	
	private boolean isReady;
	
	private String estimatedRuntime;
	
	private boolean needsBootstrapping;
	
	public ComputationServiceItem(String name, String function, boolean isReady, String estimatedRuntime, boolean needsBootstrapping) {
		this.name = name;
		this.function = function;
		this.isReady = isReady;
		this.estimatedRuntime = estimatedRuntime;
		this.needsBootstrapping = needsBootstrapping;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
	public String getEstimatedRuntime() {
		return estimatedRuntime;
	}

	public void setEstimatedRuntime(String estimatedRuntime) {
		this.estimatedRuntime = estimatedRuntime;
	}
	
	public boolean needsBootstrapping() {
		return needsBootstrapping;
	}

	public void setNeedsBootstrapping(boolean needsBootstrapping) {
		this.needsBootstrapping = needsBootstrapping;
	}

}