package com.mibs.mars.dao;


import org.apache.commons.lang3.builder.ToStringBuilder;

public class InvitationsDAO {
	private Long id;
	private Long patientid;
	private Long doctorid;
	private Long explorationid;
	private String date;
	private String comments;
	private String doctorname;
	private String doctorwork;
	private String doctorphoto;
	
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getPatientid() {
		return patientid;
	}
	public void setPatientid(Long i) {
		patientid = i;
	}
	public Long getDoctorid() {
		return doctorid;
	}
	public void setDoctorid(Long i) {
		doctorid = i;
	}
	public Long getExplorationid() {
		return explorationid;
	}
	public void setExplorationid(Long i) {
		explorationid = i;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String i) {
		date = i;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String i) {
		comments = i;
	}
	public String getDoctorname() {
		return doctorname;
	}
	public void setDoctorname(String i) {
		doctorname = i;
	}
	public String getDoctorphoto() {
		return doctorphoto;
	}
	public void setDoctorphoto(String i) {
		doctorphoto = i;
	}
	
	public String getDoctorwork() {
		return doctorwork;
	}
	public void setDoctorwork(String i) {
		doctorwork = i;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
