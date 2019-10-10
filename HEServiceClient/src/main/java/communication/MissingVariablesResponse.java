package communication;

public class MissingVariablesResponse {
	
	String[] missingVariables;
	
	public MissingVariablesResponse(String[] missingVariables) {
		setMissingVariables(missingVariables);
	}

	public String[] getMissingVariables() {
		return missingVariables;
	}

	public void setMissingVariables(String[] missingVariables) {
		this.missingVariables = missingVariables;
	}

}
