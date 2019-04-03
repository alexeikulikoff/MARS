package com.mibs.mars.utils;

public class AppConfigDAO {

	String urlprefix;
	public AppConfigDAO(String u) {
		urlprefix = u;
	}
	public void setUrlPrefix(String s) {
		urlprefix = s;
	}
	public String getUrlPrefix() {
		return urlprefix;
	}
}
