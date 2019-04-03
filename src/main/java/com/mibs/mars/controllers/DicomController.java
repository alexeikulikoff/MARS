package com.mibs.mars.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mibs.mars.config.AppConfig;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ExplorationShortRepository;

@Controller
public class DicomController {
	static Logger logger = LoggerFactory.getLogger(DicomController.class);
	@Autowired
	AppConfig appConfig;	
	@Autowired
	private ExplorationRepository explorationRepository;
	@Autowired
	private ExplorationShortRepository explorationShortRepository;
	
	@RequestMapping(value = { "/processDicomArchive" } ,method = {RequestMethod.GET})
	public @ResponseBody QueryResult processDicomArchive(@RequestParam(value="id", required = true)  String id ){
	    try{
	    	 Exploration exploration = explorationRepository.findOne(Long.parseLong(id));
	    	 byte[] arr = exploration.geDicom();
	    	 ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( arr );
	    	 FileOutputStream foutput = new FileOutputStream(new File(appConfig.getStoragePath() + "/" + exploration.getDicomname()));
	    	 int length;
	         byte[] buffer = new byte[1024];
	         while ((length = byteArrayInputStream.read(buffer)) != -1){
	        	 foutput.write(buffer, 0, length);
	         }
	         foutput.close();
	    	 return new QueryResult("fine");
	    	 
		     
	    }catch(Exception e){
	    	return null;
	    }
	}
	
}
