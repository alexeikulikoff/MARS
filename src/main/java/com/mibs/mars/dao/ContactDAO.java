package com.mibs.mars.dao;

public class ContactDAO {
	private Long id;
	private String email;
	private String surname; 
	private String firstname; 
	private String lastname; 
	private String childwork;
	private String childphone;
	private String childpost;
	private String photo; 
	private String state; 
	private Long accessed;
	private Long hidden;
	
	public ContactDAO() {}
	
	public ContactDAO(String s) {
		state = s;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public String getState() {
		return state;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String e) {
		email = e;
	}
	public String getSurname() {
		return this.surname;
	}
	public void setSurname(String e) {
		this.surname = e;
	}
	public String getFirstname() {
		return this.firstname;
	}
	public void setFirstname(String e) {
		this.firstname = e;
	}
	public String getLastname() {
		return this.lastname;
	}
	public void setLastname(String e) {
		this.lastname = e;
	}
	public String getChildwork() {
		return childwork;
	}
	public void setChildwork(String i) {
		childwork = i;
	}
	public String getChildphone() {
		return childphone;
	}
	public void setChildphone(String i) {
		childphone = i;
	}
	public String getChildpost() {
		return childpost;
	}
	public void setChildpost(String i) {
		childpost = i;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String b) {
		photo = b;
	}
	public Long getAccessed() {
		return accessed;
	}
	public void setAccessed(Long i) {
		accessed = i;
	}
	public Long getHidden() {
		return hidden;
	}
	public void setHidden(Long i) {
		hidden = i;
	}
}
