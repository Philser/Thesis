package communication;

public class ComputationResultResponse {
	
	public String result;
	public String status;
	
	public String isComputationDone() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ComputationResultResponse(String result, String status) {
		setResult(result);
		setStatus(status);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
