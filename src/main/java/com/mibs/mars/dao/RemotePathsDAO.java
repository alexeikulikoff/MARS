package com.mibs.mars.dao;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemotePathsDAO {
	private Long id;
	private String login;
	private String passwd;
	private String department;
	private String ipaddress;
	private String hostname;
	private String dirname;
	private String uniqueid;
	
	public RemotePathsDAO() {}
	
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin( String s) {
		login = s;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd( String s) {
		passwd = s;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment( String s) {
		department = s;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress( String s) {
		ipaddress = s;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname( String s) {
		hostname = s;
	}
	public String getDirname() {
		return dirname;
	}
	public void setDirname( String s) {
		dirname = s;
	}
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid( String s) {
		uniqueid = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
	
}
