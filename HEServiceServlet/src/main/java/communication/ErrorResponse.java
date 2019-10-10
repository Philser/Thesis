package communication;

public class ErrorResponse {
	
	private final String status = "error";
	
	private String errorName;
	
	private String errorMessage;

	public ErrorResponse(String errorName, String errorMessage) {
		this.errorName = errorName;
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
