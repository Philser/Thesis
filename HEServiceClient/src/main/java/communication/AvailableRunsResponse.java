package communication;

import java.util.List;

import data.ServiceRunItem;

public class AvailableRunsResponse {
	
	private List<ServiceRunItem> runs;

	public AvailableRunsResponse(List<ServiceRunItem> runs) {
		this.runs = runs;
	}

	public List<ServiceRunItem> getRuns() {
		return runs;
	}

	public void setRuns(List<ServiceRunItem> runs) {
		this.runs = runs;
	}
}
