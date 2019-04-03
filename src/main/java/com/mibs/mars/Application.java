package com.mibs.mars;

import java.net.MalformedURLException;
import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import jcifs.smb.SmbFile;

//@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) throws SQLException {
        SpringApplication.run(Application.class, args);
      
    	
    	
    }
}
