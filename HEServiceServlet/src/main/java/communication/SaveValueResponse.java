package communication;

public class SaveValueResponse {
	
	private final String status = "success";
	
	private String message;

	public SaveValueResponse(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
