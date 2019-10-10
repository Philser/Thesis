package data;

/**
 * 
 * @author D072531 - Philip Kaiser
 * @brief Contains information about an existing run of a computation service.
 */
public class ServiceRunItem {
	private String runId;

	private String peerGroupId;
	
	/**
	 * 
	 * @param runId The ID of the run.
	 * @param peerGroupId The ID of the peer group this run is associated with.
	 * 
	 * Contains information about an existing run of a computation service.
	 */
	public ServiceRunItem(String runId, String peerGroupId) {
		this.runId = runId;
		this.peerGroupId = peerGroupId;
	}
	
	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}
}