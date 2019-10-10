package communication;

public class CreateRunRequest {
	
	private String peerGroupId;

	public String getPeerGroupId() {
		return peerGroupId;
	}

	public void setPeerGroupId(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}

	public CreateRunRequest(String peerGroupId) {
		this.peerGroupId = peerGroupId;
	}

}
