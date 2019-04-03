package com.mibs.mars.controllers;

public class ProfileRequest {
	
	private String email;

	
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString(){
		return "PatientRequest: [ email : " + email + " ]"; 
	}
}
