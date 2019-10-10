package communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableRunsResponse {
	
	private List<Run> runs = new ArrayList<>();

	public AvailableRunsResponse(Map<String, String> runs) {
		this.runs = convertRuns(runs);
	}

	public List<Run> getRuns() {
		return runs;
	}

	public void setRuns(List<Run> runs) {
		this.runs = runs;
	}
	
	private List<Run> convertRuns(Map<String, String> runs) {
		List<Run> runsList = new ArrayList<>();
		String runId, peerGroupId;
		for(String key: runs.keySet()) {
			runId = key;
			peerGroupId = runs.get(key);
			Run run = new Run(runId, peerGroupId);
			runsList.add(run);
		}
		
		return runsList;
	}
	
	private class Run {
		public String runId;
		public String peerGroupId;
		
		public Run(String runId, String peerGroupId) {
			this.runId = runId;
			this.peerGroupId = peerGroupId;
		}
	}

}
