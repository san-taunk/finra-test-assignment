package finra.dto;

public class GrantReadAccessDto {

	private String userId;
	private String NodeRef;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNodeRef() {
		return NodeRef;
	}
	public void setNodeRef(String nodeRef) {
		NodeRef = nodeRef;
	}
}
