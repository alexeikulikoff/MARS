package com.mibs.mars.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

public class RequestImage {

	public MultipartFile image;
	public String email;
	
	public void setImage(MultipartFile i){
		image = i;
	}
	public void setEmail(String e){
		email  = e;
	}
	public MultipartFile getImage(){
		return image;
	}
	public String getEmail(){
		return email;
	}
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
