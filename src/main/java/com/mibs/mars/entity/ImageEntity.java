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
@Table(name = "images")
public class ImageEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "images_id_seq"
    )
	@Column(name = "id")
	private Long id;
	@Column(name = "explorationid")
	private Long explorationid;
	@Column(name = "serial")
	private Long serial;
	@Column(name = "inst")
	private Long inst;
	
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getExplorationid() {
		return explorationid;
	}
	public void setExplorationid(Long i) {
		explorationid = i;
	}
	public Long getSerial() {
		return serial;
	}
	public void setSerial(Long i) {
		serial = i;
	}
	public Long getInst() {
		return inst;
	}
	public void setInst(Long i) {
		inst = i;
	}

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
	
}
