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

import com.mibs.mars.dao.RemotePathsDAO;

@Entity
@Table(name = "paths")
public class RemotePaths implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "paths_id_seq"
    )
	private Long id;
	@Column(name = "login")
	private String login;
	@Column(name = "passwd")
	private String passwd;
	@Column(name = "department")
	private String department;
	@Column(name = "ipaddress")
	private String ipaddress;
	@Column(name = "hostname")
	private String hostname;
	@Column(name = "dirname")
	private String dirname;
	@Column(name = "checked")
	private Integer checked;
	@Column(name = "uniqueid")
	private String uniqueid;
	
	public RemotePaths() {}
	
	public RemotePaths(RemotePathsDAO rp) {
		login= rp.getLogin();
		passwd = rp.getPasswd();
		department = rp.getDepartment();
		ipaddress = rp.getIpaddress();
		dirname = rp.getDirname();
		uniqueid = null;
		checked = 0;
		
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
	public Integer getChecked() {
		return checked;
	}
	public void setChecked( Integer s) {
		checked = s;
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
