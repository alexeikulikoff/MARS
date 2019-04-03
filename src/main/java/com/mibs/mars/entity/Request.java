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
@Table(name = "request")
public class Request implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "request_id_seq"
    )
	@Column(name = "id")
	private Long id;
	@Column(name = "connectionstring")
	private String connectionString;
	@Column(name = "serviceaddress")
	private String serviceAddress;
	@Column(name = "email")
	private String email;
	@Column(name = "first")
	private String first;
	@Column(name = "parent")
	private String parent;
	@Column(name = "family")
	private String family;
	@Column(name = "uid")
	private String uid;
	@Column(name = "studyname")
	private String studyName;
	@Column(name = "path")
	private String path;
	@Column(name = "ispaid")
	private String isPaid;
	@Column(name = "code")
	private String code;
	@Column(name = "count")
	private String count;
	@Column(name = "sumunit")
	private String sumUnit;
	@Column(name = "date")
	private String date;
	@Column(name = "curdate")
	private Long curdate;
	@Column(name = "xml")
	private byte[] xml;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
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
	public Long getCurdate() {
		return curdate;
	}
	public void setCurdate(Long s) {
		curdate = s;
	}
	public byte[] getXml() {
		return xml;
	}
	public void setXml(byte[] s) {
		xml = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
