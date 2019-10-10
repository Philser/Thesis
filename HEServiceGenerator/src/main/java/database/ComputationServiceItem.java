package database;

/**
 * Represents a single service.
 * @author Philip Kaiser
 *
 */
public class ComputationServiceItem {
	
	private String name;
	
	private String function;
	
	private boolean isReady;
	
	private String estimatedRuntime;
	
	private boolean needsBootstrapping;
	
	/**
	 * Represents a single service.
	 * @param name Name of the service
	 * @param function Arithmetic function the service evaluates
	 * @param isReady True, if the service is done being deployed
	 * @param estimatedRuntime Time estimation of a single computation run
	 * @param needsBootstrapping True, if the service needs to bootstrap while evaluate.
	 */
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
}