package communication;

public class CreateRunResponse {
	
	private final String status = "created";
	
	private String runId;

	public CreateRunResponse(String runId) {
		this.runId = runId;
	}

	public String getStatus() {
		return status;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

}
