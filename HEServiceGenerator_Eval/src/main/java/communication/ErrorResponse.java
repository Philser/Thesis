package communication;

/**
 * Response for every kind of error appearing throughout the client-service interaction.
 * @author Philip Kaiser
 *
 */
public class ErrorResponse {

	private String status = "error";
	private String errorName;
	private String errorMessage;
	
	/**
	 * Response for every kind of error appearing throughout the client-service interaction.
	 * @param errorName Name of the error
	 * @param errorMessage Detailed error message
	 */
	public ErrorResponse(String errorName, String errorMessage) {
		this.errorName = errorName;
		this.errorMessage = errorMessage;
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
