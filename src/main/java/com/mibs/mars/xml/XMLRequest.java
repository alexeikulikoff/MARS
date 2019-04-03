package com.mibs.mars.xml;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class XMLRequest {

	private String connectionString;
	private String serviceAddress;
	private String email;
	private String first;
	private String parent;
	private String family;
	private String uid;
	private String studyName;
	private String path;
	private String isPaid;
	private String code;
	private String sumUnit;
	private String date;
	private String count;
	
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionString(String s) {
		connectionString = s;
	}
	public String getServiceAddress() {
		return serviceAddress;
	}
	public void setServiceAddress(String s) {
		serviceAddress = s;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String s) {
		email = s;
	}
	public String getFirst() {
		return first;
	}
	public void setFirst(String s) {
		first = s;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String s) {
		parent = s;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String s) {
		family = s;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String s) {
		uid = s;
	}
	public String getStudyName() {
		return studyName;
	}
	public void setStudyName(String s) {
		studyName = s;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String s) {
		path = s;
	}
	public String getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(String s) {
		isPaid = s;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String s) {
		code = s;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String s) {
		count = s;
	}

	public String getSumUnit() {
		return sumUnit;
	}
	public void setSumUnit(String s) {
		sumUnit = s;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String s) {
		date = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
