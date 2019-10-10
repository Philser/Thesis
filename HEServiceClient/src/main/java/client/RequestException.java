package client;

public class RequestException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private int responseCode;

	private String responseMessage;
	
	public RequestException(int responseCode, String responseMessage) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}
}
