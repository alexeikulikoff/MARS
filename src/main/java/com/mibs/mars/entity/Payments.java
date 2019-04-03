package com.mibs.mars.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import static com.mibs.mars.utils.MUtils.UnixTimeToStringDateOnly;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "payments")
public class Payments implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sequence-generator"
    )
    @SequenceGenerator(
        name = "sequence-generator",
        sequenceName = "payments_id_seq"
    )
	private Long id;
	@Column(name = "userid")
	private Long userid;
	@Column(name = "paidtill")
	private Long paidtill; 
	@Column(name = "paiddate")
	private Long paiddate; 
	@Column(name = "paidsum")
	private Float paidsum; 
	@Column(name = "comments")
	private String comments; 
	
	public Long getId() {
		return id;
	}
	public void setId(Long i) {
		id = i;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long i) {
		userid = i;
	}
	public String getPaidtill() {
		return UnixTimeToStringDateOnly( paidtill);

	}
	public void setPaidtill(Long e) {
		
		this.paidtill = e;
	}
	public String getPaiddate() {
		return UnixTimeToStringDateOnly( paiddate );
	
	}
	public void setPaiddate(Long e) {
		this.paiddate = e;
	}
	public Float getPaidsum() {
		
		return this.paidsum;
	}
	public void setPaidsum(Float e) {
		this.paidsum = e;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String s) {
		comments = s;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
