package com.mibs.mars.controllers;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.mibs.mars.entity.RemotePaths;
import com.mibs.mars.entity.Users;
import com.mibs.mars.exception.ErrorCopyFiles;
import com.mibs.mars.exception.ErrorCreateProfileException;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exception.FolderNotFoundException;
import com.mibs.mars.exceptions.CabinetBuildException;
import com.mibs.mars.exceptions.ErrorTransferDICOMException;
import com.mibs.mars.exceptions.TransferDicomException;
import com.mibs.mars.net.MailAgent;
import com.mibs.mars.repository.ConclusionRepository;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ExplorationShortRepository;
import com.mibs.mars.repository.ImagesRepository;
import com.mibs.mars.repository.UsersRepository;
import com.mibs.mars.service.ExplorationUniqueName;
import com.mibs.mars.service.UsersDetails;
import com.mibs.mars.utils.Dcm2Img;
import com.mibs.mars.utils.DicomHandler;
import com.mibs.mars.utils.MUtils;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import static java.nio.file.StandardCopyOption.*;

import static com.mibs.mars.utils.Messages.*;
import static com.mibs.mars.utils.MUtils.regDate;
@Controller
public class DownloadController extends AbstractController{
	private final String ADMIN_EMAIL = "storage@mcomtech.ru";
	private Locale locale = Locale.getDefault();
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
	 public @ResponseBody QueryResult saveExplorationViaNetwork(@RequestBody ExplorationNew exp ){
		
		 if ((exp.getName().length()== 0 ) | (exp.getUsername().length()== 0 ) | (exp.getPassword().length() == 0 ) | (exp.getName().length() == 0) | (exp.getPath().length() == 0 ) | (exp.getUserid().length() == 0)) {
			 return new QueryResult(ERROR_EXPLORATION_SAVE); 
		 }
		 String uniqueID =  new ExplorationUniqueName().getUniqueName();
		 Exploration exploration = new Exploration();
		 exploration.setUsersId(Long.parseLong(exp.getUserid()));
		 exploration.setDate(regDate());
		 exploration.setDicomname( uniqueID);
		 exploration.setDicomSize(new Long(0));
		 exploration.setExplname( exp.getName());
		 exploration.setRemotepath( exp.getPath());
		 exploration.setUniqueid( uniqueID );
		 Exploration saved = explorationRepository.save(exploration) ;

		 return buildCabinet2(exp, saved);
		 
	 	}
		@RequestMapping(value = { "/loadExploration" },method = {RequestMethod.GET})
		public @ResponseBody QueryResult  loadExploration( @RequestParam(value="id", required = true)  Long id  ) {
			Exploration explorations = explorationRepository.findById( id );
			if ( explorations == null  ) {
				return new QueryResult( "ERROR_CABINET_BUILDING" );
			}
			if (explorations.getDicomSize() == 0) {
				String UniqueID = explorations.getUniqueid(); 
				String serDir  = appConfig.getSerializedPath() + "/" + UniqueID;
				File seFile = new File(serDir);
				if ( seFile.isDirectory()) {
					deleteDir( seFile.toPath() );
					logger.info("Directory " + serDir + " has been deleted." );
				}
				String dicomDir  = appConfig.getStoragePath() + "/" + UniqueID;
				File deFile = new File(dicomDir);
				if( deFile.isDirectory()) {
					deleteDir( deFile.toPath() );
					logger.info("Directory " + serDir + " has been deleted." );
				}
				if (explorations.getRemotepath().trim().length() > 0) {
					String remotePath = explorations.getRemotepath().trim();
					String s1 = remotePath.replaceAll("\\\\", "/");
					Path path = Paths.get( s1 );
					String ipAddress = path.getName(0).toString();
					String dirName = path.getName(1).toString().toLowerCase();
					RemotePaths remotePaths = remotePathsRepository.findByIpaddressAndDirnameIgnoreCase(ipAddress, dirName);
					if (remotePaths == null) {
						logger.error("Error! Remoute path is null!");
						return new QueryResult( "ERROR_CABINET_BUILDING" );
					}
					ExplorationNew exp = new ExplorationNew();
				    exp.setUsername( remotePaths.getLogin() );
				    exp.setPassword( remotePaths.getPasswd() );
				    exp.setPath( s1.startsWith("//") ? s1.substring(2) : s1);
					return buildCabinet2(exp, explorations);
				}else {
					logger.error("Error! Exploration does not have remote path");
					return new QueryResult( "ERROR_CABINET_BUILDING" );	
				}
			}else {
				logger.error("Error! Dicom size is not equal to '0' ");
				return new QueryResult( "ERROR_CABINET_BUILDING" );		
			}
		} 
		@RequestMapping(value = { "/buildCabinet" },method = {RequestMethod.GET})
		public @ResponseBody QueryResult  buildCabinet( @AuthenticationPrincipal UsersDetails activeUser  ) {
			Users user = usersRepository.findByEmail( activeUser.getEmail() );
			if (user == null) return new QueryResult("ERROR_USER_NOT_FOUND");
			
			List<Exploration> explorations = explorationRepository.findByUsersId( user.getId());
			if (explorations != null && explorations.size() > 0) {
				for(Exploration expl : explorations) {
					if (expl.getDicomSize() == 0) {
						try {
							String remotePath = expl.getRemotepath().trim();
							String s1 = remotePath.replaceAll("\\\\", "/");
							Path path = Paths.get( s1 );
							String ipAddress = path.getName(0).toString();
							String dirName = path.getName(1).toString().toLowerCase();
							RemotePaths remotePaths = remotePathsRepository.findByIpaddressAndDirnameIgnoreCase(ipAddress, dirName);
							if (remotePaths == null) {
								logger.error("Error! Remoute path is null!");
								return new QueryResult( "ERROR_CABINET_BUILDING" );
							}
							ExplorationNew exp = new ExplorationNew();
						    exp.setUsername( remotePaths.getLogin() );
						    exp.setPassword( remotePaths.getPasswd() );
						    exp.setPath( s1.startsWith("//") ? s1.substring(2) : s1);
							return buildCabinet2(exp, expl);
						} catch (Exception e) {
							 logger.error("Error building Cabinet with message: " + e.getMessage() );
							 String[] params = { user.getSurname() + "  " + user.getFirstname() + " " + user.getLastname(), user.getEmail()};
							 String template = messageSource.getMessage("mail.template.newCabinetError", params, locale);
							 String subject =  messageSource.getMessage("mail.template.subject", null, locale);
							 try {
								MailAgent.sendMail(appConfig.getMailFrom(), ADMIN_EMAIL, appConfig.getMaiSmtpHost(),  subject, "", template);
							} catch (MessagingException e1) {
								 logger.error("Error sending email to " + ADMIN_EMAIL );
							}
							return new QueryResult( "ERROR_CABINET_BUILDING" );
						}
					}
				}
			}
			return new QueryResult("CABINET_BUILDED");
			
		}

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
		@RequestMapping(value = { "/rebuild" },method = {RequestMethod.GET})
		public @ResponseBody QueryResult  rebuild( @RequestParam(value="id", required = true)  Long id  ) {
			Exploration explorations = explorationRepository.findById( id );
			if (explorations == null) return new QueryResult("ERROR_CABINET_REBUILD");
			
			Path path = Paths.get( appConfig.getStoragePath() + "/" + explorations.getDicomname() );
			Path serPath = Paths.get( appConfig.getSerializedPath() + "/" + explorations.getDicomname() );
			if (Files.exists(path)) {
				try {
					Files.walk(path)
					  .sorted(Comparator.reverseOrder())
					  .map(Path::toFile)
					  .forEach(File::delete);
					
				} catch (IOException e) {
					return new QueryResult("ERROR_CABINET_REBUILD");
				}
			}
			if (Files.exists(serPath)) {
				try {
					Files.walk(serPath)
					  .sorted(Comparator.reverseOrder())
					  .map(Path::toFile)
					  .forEach(File::delete);
					explorationRepository.updateDicomSize(new Long(0), explorations.getId());
				} catch (IOException e) {
					return new QueryResult("ERROR_CABINET_REBUILD");
				}	
			}
			return new QueryResult("CABINET_REBUILDED");
			
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
	
		File tempDir = new File(unzippedPath.toAbsolutePath().toString()) ;
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
