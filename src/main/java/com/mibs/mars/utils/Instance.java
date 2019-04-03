package com.mibs.mars.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Instance implements Comparable<Instance>{
	public Long instNum;
	byte[] image;
	private SerializedDicom sdic;
	
	public Instance(Long i, byte[] b, SerializedDicom d) {
		
		instNum = i;
		image = b;
		sdic = d;
		
	}
	public SerializedDicom getSerializedDicom() {
		return sdic;
	}
	public Long getInstNum() {
		return instNum;
	}
	public byte[] getImage() {
		return image;
	}
	@Override
	public int compareTo(Instance o) {
		return (int)(instNum - o.instNum);
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}	
}
