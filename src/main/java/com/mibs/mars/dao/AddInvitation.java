package com.mibs.mars.dao;

public class AddInvitation {

	private Long explorationid;
	private Long contactid;
	private String comments;
	public void setExplorationid( Long i) {
		explorationid = i;
	}
	public Long getExplorationid() {
		return explorationid;
	}
	public void setContactid( Long i) {
		contactid = i;
	}
	public Long getContactid() {
		return contactid;
	}
	public void setComments( String i) {
		comments = i;
	}
	public String getComments() {
		return comments;
	}
	
}
