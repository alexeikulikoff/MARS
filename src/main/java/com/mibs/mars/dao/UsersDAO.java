package com.mibs.mars.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mibs.mars.entity.Payments;

public class UsersDAO {
	private Long id;
	private String login;
	private String passwd;
	private String surname; 
	private String firstname; 
	private String lastname; 
	private String email;
	private String photo; 
	private String role; 
	private Long mailed;

	private List<Payments> payments = new ArrayList<>();
	public UsersDAO() {
		
	}
	public void addPayment(Payments pm) {
		payments.add( pm );
	}
	public List<Payments> getPayments(){
		return payments;
	}
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String e) {
		email = e;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String b) {
		photo = b;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String b) {
		role = b;
	}
	public Long getMailed() {
		return mailed;
	}
	public void setMailed(Long e) {
		mailed = e;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
