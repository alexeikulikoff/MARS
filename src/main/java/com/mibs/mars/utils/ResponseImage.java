package com.mibs.mars.utils;

public class ResponseImage {

	public byte[] image;
	public ResponseImage(byte[] i){
		image = i;
	}
	public byte[] getImage(){
		return image;
	}
}
