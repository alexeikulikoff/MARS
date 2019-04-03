package com.mibs.mars.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "contacts")
public class Contacts  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "contacts_id_seq"
    )
	private Long id;
	@Column(name = "ownerid")
	private Long ownerid;
	@Column(name = "childid")
	private Long childid;
	@Column(name = "accessed")
	private Long accessed;
	@Column(name = "hidden")
	private Long hidden;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerid", insertable=false,  updatable=false)
	private Users users;
	
	public Contacts() {};
	public Contacts(Long o, Long c) {
		ownerid = o;
		childid = c;
	}
	public Users getUsers() {
		return users;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getAccessed() {
		return accessed;
	}
	public void setAccessed(Long i) {
		accessed = i;
	}
	public Long getHidden() {
		return hidden;
	}
	public void setHidden(Long i) {
		hidden = i;
	}
	public Long getChildid() {
		return childid;
	}
	public void setChildid(Long i) {
		childid = i;
	}
	public Long getOwnerid() {
		return ownerid;
	}
	public void setOwnerid(Long i) {
		ownerid = i;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
