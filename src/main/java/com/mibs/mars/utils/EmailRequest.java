package com.mibs.mars.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EmailRequest {

	private String id;
	private String email;
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String e) {
		this.email = e;
	}
	@Override
	public String toString(){
	  return ToStringBuilder.reflectionToString(this);	
	}
}
