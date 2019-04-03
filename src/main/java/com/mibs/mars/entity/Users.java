package com.mibs.mars.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mibs.mars.dao.UsersDAO;
import com.mibs.mars.utils.MUtils;

@Entity
@Table(name = "users")
public class Users  implements Serializable{

	private static final long serialVersionUID = 2L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "usr_id_seq"
    )
	private Long id;
	@Column(name = "login")
	private String login;
	@Column(name = "passwd")
	private String passwd;
	@Column(name = "email")
	private String email;
	@Column(name = "roles")
	private String roles;
	@Column(name = "regdate")
	private Long regdate;
	@Column(name = "photo")
	private byte[] photo; 
	@Column(name = "sex")
	private String sex; 
	@Column(name = "ismailchecked")
	private Long mailed;
	@Column(name = "surname")
	private String surname; 
	@Column(name = "firstname")
	private String firstname; 
	@Column(name = "lastname")
	private String lastname; 

	
	public Users() {
		
	}
	public Users(String e ) {
		email = e;
	}
	public Users(Users u) {
		id = u.id;
		passwd = u.passwd;
		regdate = u.regdate;
		roles = u.roles;
		email = u.email;
		photo= u.photo;
		sex = u.sex;
		mailed = u.mailed;
		firstname = u.firstname;
		lastname = u.lastname;
		surname = u.surname;
		login = email;
	}
	public Users(UsersDAO u, String role) {
		passwd = u.getPasswd();
		regdate = MUtils.regDate();
		roles = role;
		email = u.getEmail();
		firstname = u.getFirstname();
		lastname = u.getLastname();
		surname = u.getSurname();
		login = email;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String e) {
		email = e;
	}
	public Long getRegdate() {
		return regdate;
	}
	public void setRegdate(Long r) {
		regdate= r;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] b) {
		photo = b;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles( String s) {
		roles = s;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String e) {
		sex = e;
	}
	public Long getMailed() {
		return mailed;
	}
	public void setMailed(Long e) {
		mailed = e;
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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Users)) return false;
		Users usr = (Users) obj;
		return usr.login.equals(login);
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
