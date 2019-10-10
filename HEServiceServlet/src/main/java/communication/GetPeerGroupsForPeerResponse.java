package communication;

import java.util.List;

public class GetPeerGroupsForPeerResponse {
	private List<String> peerGroups;

	public GetPeerGroupsForPeerResponse(List<String> peerGroups) {
		this.peerGroups = peerGroups;
	}

	public List<String> getPeerGroups() {
		return peerGroups;
	}

	public void setPeerGroups(List<String> peerGroups) {
		this.peerGroups = peerGroups;
	}
}
