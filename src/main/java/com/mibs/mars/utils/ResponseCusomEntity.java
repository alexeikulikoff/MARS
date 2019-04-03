package com.mibs.mars.utils;

import java.util.ArrayList;
import java.util.List;

import com.mibs.mars.dao.ExplorationDAO;

public class ResponseCusomEntity {

	private List<ExplorationDAO> explorations;
	private String username;
	private int explorationnumber;
	public ResponseCusomEntity(String name, int n) {
		username = name;
		explorationnumber = n;
		explorations = new ArrayList<>();
	}
	public void setExplorations(List<ExplorationDAO> e) {
		explorations = e;
	}
	public List<ExplorationDAO> getExplorations(){
		return explorations;
	}
	public int getExplorationnumber() {
		return explorationnumber;
	}
	public String getUsername() {
		return username;
	}
	
	
}
