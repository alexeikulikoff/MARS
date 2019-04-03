package com.mibs.mars.entity;





import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "exploration")
public class Journal implements Serializable{
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
	@Lob @Basic(fetch=FetchType.LAZY)
	@Column(name = "dicom")
	private byte[] dicom;
	@Column(name = "dicom_size")
	private long dicomSize;
	@Column(name = "dicom_name")
	private String dicomname;
	@Column(name = "remotepath")
	private String remotepath;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", insertable=false,  updatable=false)
	private Users users;

	public Users getUsers() {
		return users;
	}
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRemotepath() {
		return this.remotepath;
	}
	public void setRemotepath(String s) {
		this.remotepath = s;
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
	public void setDicom(byte[] c) {
		dicom = c;
	}
	public byte[] geDicom() {
		return dicom;
	}
	public long getDicomSize() {
		return dicomSize;
	}
	public void setDicomSize(long c) {
		dicomSize = c;
	}
	public String getDicomname() {
		return dicomname;
	}
	public void setDicomname(String s) {
		dicomname = s;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}
