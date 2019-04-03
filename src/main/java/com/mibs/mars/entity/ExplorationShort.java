package com.mibs.mars.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "exploration")
public class ExplorationShort  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "exploration_id_seq"
    )
	@Column(name = "id")
	private Long id;
	@Column(name = "users_id")
	private Long usersId;
	@Column(name = "date")
	private Long date;
	@Column(name = "uniqueid")
	private String uniqueid;
	@Column(name = "explname")
	private String explname;
	@Column(name = "dicom_size")
	private long dicomSize;

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUsersId() {
		return usersId;
	}
	public void setUsersId(Long id) {
		usersId = id;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long d) {
		date = d;
	}
	public String getUniqueid() {
		return uniqueid;
	}
	public void setUniqueid(String s) {
		uniqueid = s;
	}
	
	public String getExplname() {
		return explname;
	}
	public void setExplname(String s) {
		explname = s;
	}
	public long getDicomSize() {
		return dicomSize;
	}
	public void setDicomSize(Long s) {
		dicomSize = s;
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExplorationShort)) return false;
		ExplorationShort expl = (ExplorationShort) obj;
		return expl.uniqueid.equals(uniqueid);
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
