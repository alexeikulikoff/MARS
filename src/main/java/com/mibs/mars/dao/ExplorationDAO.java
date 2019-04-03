package com.mibs.mars.dao;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.ExplorationShort;
import com.mibs.mars.utils.MUtils;

public class ExplorationDAO {
	private Long id;
	private Long usersId;
	private String date;
	private String uniqueid;
	private String explname;
	private Long dicomsize;

	private List<ConclusionDAO> conclusions;
	
	public ExplorationDAO(Exploration e ) {
		id = e.getId();
		usersId = e.getUsersId();
		uniqueid = e.getUniqueid();
		explname = e.getExplname();
		dicomsize = e.getDicomSize();
		date = MUtils.UnixTimeToStringDateOnly(e.getDate());
		conclusions = new ArrayList<>();
	}
	public ExplorationDAO(ExplorationShort e) {
		id = e.getId();
		usersId = e.getUsersId();
		uniqueid = e.getUniqueid();
		explname = e.getExplname();
		dicomsize = e.getDicomSize();
		date = MUtils.UnixTimeToStringDateOnly(e.getDate());
		conclusions = new ArrayList<>();
	}
	public List<ConclusionDAO> getConclusions(){
		return conclusions;
	}
	public void addConclusions(ConclusionDAO s) {
		conclusions.add(s);
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDicomsize() {
		return dicomsize;
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
