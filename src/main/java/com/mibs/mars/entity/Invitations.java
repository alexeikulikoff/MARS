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
@Table(name = "invitations")
public class Invitations implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "invitations_id_seq"
    )
	private Long id;
	@Column(name = "patientid")
	private Long patientid;
	@Column(name = "doctorid")
	private Long doctorid;
	@Column(name = "explorationid")
	private Long explorationid;
	@Column(name = "date")
	private Long date;
	@Column(name = "comments")
	private String comments;
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
	public Long getExplorationid() {
		return explorationid;
	}
	public void setExplorationid(Long i) {
		explorationid = i;
	}
	public Long getDoctorid() {
		return doctorid;
	}
	public void setDoctorid(Long i) {
		doctorid = i;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long i) {
		date = i;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String i) {
		comments = i;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
}
