package com.mibs.mars.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class SerialInstanceImage {

	public long serial;
	public long instance;
	public byte[] image;
	public SerialInstanceImage(long s, long i, byte[] b) {
		serial = s;
		instance = i;
		image = b;
	}
	public long getSerial() {
		return serial;
	}
	public long getInstance() {
		return instance;
	}
	public byte[] getImage() {
		return image;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
}
