package com.mibs.mars.dao;

import com.mibs.mars.entity.Payments;

public class PaymentDAO {

	private String email;
	private String sum;
	private String comments; 
	private int period;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String s) {
		email = s;
	}
	public String getSum() {
		return sum;
	}
	public void setSum(String s) {
		sum = s;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String s) {
		comments = s;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int p) {
		period = p;
	}
	
}
