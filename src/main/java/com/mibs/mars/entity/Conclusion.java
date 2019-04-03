package com.mibs.mars.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "conclusion")
public class Conclusion implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "conclusion_id_seq"
    )
	private Long id;
	private Long explorationid;
	private byte[] conclusionfile;
	private String filename;
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getExplorationid() {
		return explorationid;
	}
	public void setExplorationid( Long s) {
		explorationid = s;
	}
	public byte[] getConclusionfile() {
		return conclusionfile;
	}
	public void setConclusionfile( byte[] s) {
		conclusionfile = s;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename( String s) {
		filename = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
