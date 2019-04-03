package com.mibs.mars.dao;

import java.util.ArrayList;
import java.util.List;

public class UsersWrapper {

	private List<UsersDAO> data;
	public UsersWrapper() {
		data = new ArrayList<>();
	}
	public void addData(UsersDAO u) {
		data.add( u );
	}
	public List<UsersDAO> getData() {
		return data; 
	}
}
