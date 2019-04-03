package com.mibs.mars.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "checked_mail")
public class CheckedEmail implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "email")
	private String email;
	@Column(name = "uuid")
	private String uuid;
	@Column(name = "t1")
	private Long t1;
	
	public CheckedEmail(){}
	
	public CheckedEmail(String e, String u){
		email = e;
		uuid = u;
		t1 = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toEpochSecond();
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String e) {
		this.email = e;
	}
	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String e) {
		this.uuid = e;
	}
	public Long getT1() {
		return this.t1;
	}
	public void setT1(Long t) {
		this.t1 = t;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
