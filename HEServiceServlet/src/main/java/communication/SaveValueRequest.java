package communication;

public class SaveValueRequest {

	
	private String peerId;
	
	private String varName;
	
	private String varValue;

	public SaveValueRequest(String peerId, String varName, String varValue) {
		this.peerId = peerId;
		this.varName = varName;
		this.varValue = varValue;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public String getVarValue() {
		return varValue;
	}

	public void setVarValue(String varValue) {
		this.varValue = varValue;
	}
}
