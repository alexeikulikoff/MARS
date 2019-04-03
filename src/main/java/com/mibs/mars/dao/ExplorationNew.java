package com.mibs.mars.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ExplorationNew {
	private String name; 
	private String path; 
	private String username; 
	private String password;
	private String userid;
	
	public ExplorationNew() {}
	
	public void setUserid(String s) {
		userid = s;
	}
	public String getUserid() {
		return userid;
	}
	public void setName(String s) {
		name = s;
	}
	public String getName() {
		return name;
	}
	public void setPath(String s) {
		path = s;
	}
	public String getPath() {
		return path.lastIndexOf("/") < ( path.length() -1 ) ? path + "/" : path;
	}
	public void setUsername(String s) {
		username = s;
	}
	public String getUsername() {
		return username;
	}
	public void setPassword(String s) {
		password = s;
	}
	public String getPassword() {
		return password;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
