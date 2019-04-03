package com.mibs.mars.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class AppConfig {
	@Value("${mars.hadoop_user}")
	private String hadoop_user;
	
	@Value("${mars.hadoop_dicom_path")
	private String hadoop_dicom_path;
	
	@Value("${mars.hadoop_ser_path")
	private String hadoop_ser_path;
	
	@Value("${mars.hadoop_con_path")
	private String hadoop_con_path;

	@Value("${mars.storage_path}")
	private String storagePath;
	
	@Value("${mars.serialized_path}")
	private String serializedPath;
	
	@Value("${mars.mail_confirm_period}")
	private String maiConfirmPeriod;
	
	@Value("${mars.mail_smtp_host}")
	private String maiSmtpHost;
	
	@Value("${mars.mail_port}")
	private String mailPort;
	
	@Value("${mars.mail_from}")
	private String mailFrom;
	
	@Value("${server.address}")
	private String serverAddress;
	
	@Value("${server.port}")
	private String serverPort;
	
	@Value("${server.context-path}")
	private String contextPath;
	public String getStoragePath(){
		return storagePath;
	}
	public String getHadoop_user() {
		return hadoop_user;
	}
	public String getHadoop_dicom_path() {
		return hadoop_dicom_path;
	}

	public String getHadoop_ser_path() {
		return hadoop_ser_path;
	}
	public String getHadoop_con_path() {
		return hadoop_con_path;
	}
	public String getSerializedPath(){
		return serializedPath;
	}
	public void  setStoragePath(String s){
		storagePath = s;
	}
	public String getMaiConfirmPeriod(){
		return maiConfirmPeriod;
	}
	public String getMaiSmtpHost(){
		return maiSmtpHost;
	}
	public String getMailPort(){
		return mailPort;
	}
	public String getMailFrom(){
		return mailFrom;
	}
	public String getServerAddress(){
		return serverAddress;
	}
	public String getServerPort(){
		return serverPort;
	}
	public String getContextPath(){
		return contextPath;
	}
}
