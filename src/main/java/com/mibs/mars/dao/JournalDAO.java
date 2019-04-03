package com.mibs.mars.dao;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.mibs.mars.entity.Users;
import static com.mibs.mars.utils.MUtils.UnixTimeToStringDateOnly;

public class JournalDAO {
	
	private Long id;
	private Long usersId;
	private Long date;
	private String uniqueid;
	private String explname;
	private byte[] dicom;
	private long dicomSize;
	private String dicomname;
	private String remotepath;
	private UsersDAO users;

	public UsersDAO getUsers() {
		return users;
	}
	public void setUsers(UsersDAO u) {
		users = u;
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
	public String getDate() {
		return UnixTimeToStringDateOnly(date);
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
