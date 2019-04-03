package com.mibs.mars.controllers;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mibs.mars.config.AppConfig;
import com.mibs.mars.dao.ExplorationNew;
import com.mibs.mars.entity.Conclusion;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.Users;
import com.mibs.mars.exception.ErrorCopyFiles;
import com.mibs.mars.exception.ErrorCreateProfileException;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exception.FolderNotFoundException;
import com.mibs.mars.exceptions.CabinetBuildException;
import com.mibs.mars.repository.ConclusionRepository;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ExplorationShortRepository;
import com.mibs.mars.repository.ImagesRepository;
import com.mibs.mars.repository.UsersRepository;
import com.mibs.mars.service.ExplorationUniqueName;
import com.mibs.mars.utils.Dcm2Img;
import com.mibs.mars.utils.DicomHandler;
import com.mibs.mars.utils.MUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import static java.nio.file.StandardCopyOption.*;

import static com.mibs.mars.utils.Messages.*;
import static com.mibs.mars.utils.MUtils.regDate;
@Controller
public class DownloadController extends AbstractController{
	
	static Logger logger = LoggerFactory.getLogger(DownloadController.class);
	@Autowired
	AppConfig appConfig;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired	
	private ExplorationRepository explorationRepository;
	@Autowired
	private ExplorationShortRepository explorationShortRepository;
	@Autowired
	private ConclusionRepository conclusionRepository;
	@Autowired
	private ImagesRepository imagesRepository;
	
	
	@RequestMapping(value = { "/getDicomFileName" } ,method = {RequestMethod.GET})
	public @ResponseBody String getDicomFileName(@RequestParam(value="id", required = true)  Long id ){
			Exploration exploration = explorationRepository.findById(id);
			if (exploration==null) return "NULL"; 
			return exploration.getDicomname();
		
	}
	 @RequestMapping(value = "/dicom/{id}", method = RequestMethod.GET)
	 public  ResponseEntity<InputStreamResource> downloadDicom(@PathVariable Long  id) {
	    try{
	    	 Exploration exploration = explorationRepository.findById(id);
	    	 String folderName = exploration.getDicomname();
	    	 String zipName = folderName + ".zip";
	    	 String dicomName = appConfig.getStoragePath() + "/" + folderName + "/" +  zipName;
	    	 Path path = Paths.get( dicomName );
	    	 byte[] arr = Files.readAllBytes(path);
	    	 ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( arr );
	         HttpHeaders respHeaders = new HttpHeaders();
	         respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	         respHeaders.setContentLength((int)arr.length);
	         respHeaders.setContentDispositionFormData("attachment", zipName);
	         InputStreamResource is = new InputStreamResource(byteArrayInputStream);
	         return new ResponseEntity<InputStreamResource>(is, respHeaders, HttpStatus.OK);
		     
	    }catch(Exception e){
	    	return null;
	    }
	 }
	 @RequestMapping(value = { "/saveExplorationViaNetwork" } ,method = {RequestMethod.POST})
	 public @ResponseBody QueryResult saveExplorationViaNetwork(@RequestBody ExplorationNew explorationDAO ){
		 
		 String uniqueID =  new ExplorationUniqueName().getUniqueName();
		 Exploration exploration = new Exploration();
		 exploration.setUsersId(Long.parseLong(explorationDAO.getUserid()));
		 exploration.setDate(regDate());
		 exploration.setDicomname( uniqueID);
		 exploration.setDicomSize(new Long(0));
		 exploration.setExplname( explorationDAO.getName());
		 exploration.setRemotepath( explorationDAO.getPath());
		 exploration.setUniqueid( uniqueID );
		 Exploration savedExploration = explorationRepository.save(exploration) ;
		 try {
			buildCabinet( savedExploration, explorationDAO.getUsername(),explorationDAO.getPassword() );
			return new QueryResult(SUCCESS_EXPLORATION_SAVE);
		} catch (CabinetBuildException e) {
			 return new QueryResult(ERROR_EXPLORATION_SAVE);
		}
	}
	 
	 
/*	 @RequestMapping(value = { "/saveExplorationViaNetwork" } ,method = {RequestMethod.POST})
	 public @ResponseBody QueryResult saveExplorationViaNetwork(@RequestBody ExplorationNew dao ){
		 explorationUniqueName = new ExplorationUniqueName();;
		 String dstFoldername = explorationUniqueName.getUniqueName();
		 String serPath = appConfig.getSerializedPath() + "/" + dstFoldername;
		 Long userId = Long.parseLong(dao.getUserid());
		 try {
			String dicomPath =  copyFilesToLocalDir(dao.getUsername(), dao.getPassword(), dao.getPath(), explorationUniqueName);
			Dcm2Img dcm2Img = new Dcm2Img();
			Users user = usersRepository.findById(userId);
			Exploration exploration = addExploration(explorationUniqueName,user,dao.getName());
			try {
				dcm2Img.initImageWriter("JPEG","jpeg", null, null,null);
				dcm2Img.CCCatch(dicomPath, serPath, exploration, imagesRepository);
				long size = pack(dicomPath, dicomPath + "/" + dstFoldername + ".zip");
				explorationRepository.updateDicomSize( size , exploration.getId() );
			} catch (ErrorDicomParsingException e) {
				//explorationRepository.delete(exploration);
				return new QueryResult(ERROR_EXPLORATION_SAVE); 
			}
		 } catch (FolderNotFoundException e) {
			 e.printStackTrace();
			 return new QueryResult(ERROR_EXPLORATION_SAVE);
			 
		} catch (IOException e) {
			e.printStackTrace();
			return new QueryResult(ERROR_EXPLORATION_SAVE); 
		}
	    return new QueryResult(SUCCESS_EXPLORATION_SAVE);
	}
	*/ 
	@RequestMapping(value = { "/getCinclusionFileName" } ,method = {RequestMethod.GET})
	public @ResponseBody String getCinclusionFileName(@RequestParam(value="id", required = true)  Long id ){
		try {
			Conclusion conclusion = conclusionRepository.findById(id);
			return conclusion.getFilename();
		}catch(Exception e) {
			return "NULL";
		}
	}
	 @RequestMapping(value = "/conclusion/{id}", method = RequestMethod.GET)
	 public  ResponseEntity<InputStreamResource> downloadConclusion(@PathVariable Long  id) {
		 try{
			 Conclusion conclusion = conclusionRepository.findById(id);
	    	 byte[] arr = conclusion.getConclusionfile();
	    	 ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( arr );
	         HttpHeaders respHeaders = new HttpHeaders();
	         respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	         respHeaders.setContentLength((int)arr.length);
	         respHeaders.setContentDispositionFormData("attachment", conclusion.getFilename());
	         InputStreamResource is = new InputStreamResource(byteArrayInputStream);
	         return new ResponseEntity<InputStreamResource>(is, respHeaders, HttpStatus.OK);
	    }catch(Exception e){
	    	return null;
	    }

	 }
	@RequestMapping("/uploadExploration")
	public @ResponseBody QueryResult uploadExploration(@RequestParam("uploadDicom") MultipartFile uploadDicom,  @RequestParam("explorationName") String explorationName,  @RequestParam("userid") Long userid  ) {
		
		File testStorageDicomPath = new File(appConfig.getStoragePath());	
		if (!testStorageDicomPath.exists()) {
			logger.error("Error in configuration. Create directory: " + testStorageDicomPath);
			return new QueryResult("ERROR_DICOM_SAVE");
		}

		String UniqueName = new ExplorationUniqueName().getUniqueName();
		
		String dstPath = appConfig.getStoragePath() + "/" + UniqueName;
		
		File destDir = new File(dstPath);
		if (!destDir.exists()) {
			if (!destDir.mkdir()) {
				logger.error("Error while creating directory:" + dstPath);
				return new QueryResult("ERROR_DICOM_SAVE");
			}
		}
		String zip = dstPath + "/" + UniqueName + ".zip";
		try {
			FileUtils.copyInputStreamToFile(uploadDicom.getInputStream(), new File(zip));
		} catch (IOException e3) {
			logger.error("Error savint zip file to disk :" + zip);
			return new QueryResult("ERROR_DICOM_SAVE");
		}
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zip);
			if (zipFile.isValidZipFile()) {
				zipFile.extractAll(dstPath);
				
			}else {
				logger.error("Error while opening zip file:" + zip);
				return new QueryResult("ERROR_DICOM_SAVE");
			}
		} catch (ZipException e2) {
			logger.error("Error while opening zip file:" + zip);
			return new QueryResult("ERROR_DICOM_SAVE");
		}
	
		Path unzippedPath = Paths.get(dstPath);
	
		File tempDir = new File(unzippedPath.toAbsolutePath() + "/temp");
		if (!tempDir.exists()) {
			if (!tempDir.mkdir()) {
				logger.error("Error while creating directory  :" + tempDir);
				return new QueryResult("ERROR_DICOM_SAVE");
			}
		}
		try {
			moveFiles(unzippedPath, tempDir);
		} catch (Exception e1) {
			logger.error("Error while moving files to :" + tempDir);
			return new QueryResult("ERROR_DICOM_SAVE");
		}
	
		Exploration savedExpl= null;
		Exploration exploration = null;
		exploration = explorationRepository.findByUniqueid(UniqueName);
		if (exploration == null) {
			exploration = new Exploration();
			exploration.setUsersId(userid);
			exploration.setDate(MUtils.regDate());
			exploration.setExplname(explorationName);
			exploration.setUniqueid(UniqueName);
			exploration.setDicomname(UniqueName);
			exploration.setDicomSize(uploadDicom.getSize());
			try {
				savedExpl = explorationRepository.save(exploration);
			}catch(Exception e) {
				logger.error("Error while saving exploration:" + exploration);
				return new QueryResult("ERROR_DICOM_SAVE");
			}
		}else {
			savedExpl = exploration;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
		}
		File testStorageSerPath = new File(appConfig.getSerializedPath());	
		if (!testStorageSerPath.exists()) {
			logger.error("Error in configuration. Create directory: " + testStorageSerPath);
			return new QueryResult("ERROR_DICOM_SAVE");
		}
		DicomHandler handler = new DicomHandler(UniqueName, appConfig.getSerializedPath(), appConfig.getStoragePath());
		int parsed_n = 0;
		parsed_n = handler.parsingDICOMFiles( savedExpl.getId(), imagesRepository  );
		
		try {
			deleteFiles( unzippedPath );
		} catch (IOException e) {
			logger.error("Error while deleting files in: " + unzippedPath);
		}
		logger.info("Created " + parsed_n + " parsed dicom files." );
		return new QueryResult("SUCCESS_DICOM_SAVE");
		
	}

	private void deleteFiles(Path unzippedPath) throws IOException {
		Files.walk(unzippedPath).map(Path::toFile).sorted(Comparator.reverseOrder())
		.filter(u -> !u.getName().endsWith("zip")).forEach( File::delete );
	}
	private void deleteAll(Path unzippedPath) throws IOException{
		Files.walk(unzippedPath).map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
	}
	private void moveFiles(Path unzippedPath, File tempDir) throws Exception {
		Files.walk(unzippedPath).filter(s -> s.toFile().isFile()).filter(s -> !s.toFile().getName().endsWith("zip"))
		.forEach(f -> {
			try {
				Files.move(f.toAbsolutePath(), Paths.get(tempDir + "/" + f.getFileName()), REPLACE_EXISTING);
			} catch (Exception e) {
				logger.error("Error while copying files to DICOM temp directory!");
			}
		});
		
	}
}
