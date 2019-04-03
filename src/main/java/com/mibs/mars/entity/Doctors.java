package com.mibs.mars.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "doctors")
public class Doctors implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "doctors_id_seq"
    )
	private Long id;
	@Column(name = "userid")
	private Long userid;
	@Column(name = "phone")
	private String phone;
	@Column(name = "works")
	private String works;
	@Column(name = "post")
	private String post;
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid( Long s) {
		userid = s;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone( String s) {
		phone = s;
	}
	public String getWorks() {
		return works;
	}
	public void setWorks( String s) {
		works = s;
	}
	public String getPost() {
		return post;
	}
	public void setPost( String s) {
		post = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
