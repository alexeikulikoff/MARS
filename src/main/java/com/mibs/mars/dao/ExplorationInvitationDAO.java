package com.mibs.mars.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.ExplorationShort;
import com.mibs.mars.entity.Invitations;
import com.mibs.mars.utils.MUtils;

public class ExplorationInvitationDAO {
	private Long id;
	private Long usersId;
	private String date;
	private String uniqueid;
	private String explname;
	private List<InvitationsDAO> invitations;
	
	public ExplorationInvitationDAO(Exploration e ) {
		id = e.getId();
		usersId = e.getUsersId();
		uniqueid = e.getUniqueid();
		explname = e.getExplname();
		date = MUtils.UnixTimeToStringDate(e.getDate());
		invitations = new ArrayList<>();

	}
	public ExplorationInvitationDAO(ExplorationShort e) {
		id = e.getId();
		usersId = e.getUsersId();
		uniqueid = e.getUniqueid();
		explname = e.getExplname();
		date = MUtils.UnixTimeToStringDate(e.getDate());
		invitations = new ArrayList<>();
		
	}
	public void addInvitations(InvitationsDAO i) {
		invitations.add(i);
	}
	public List<InvitationsDAO> getInvitations() {
		return invitations;
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUsersId() {
		return usersId;
	}
	public void setUsersId(Long id) {
		usersId = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String d) {
		date = d;
	}
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(String s) {
		uniqueid = s;
	}
	public String getExplname() {
		return explname;
	}
	public void setExplname(String s) {
		explname = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
