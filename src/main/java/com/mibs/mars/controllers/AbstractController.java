package com.mibs.mars.controllers;


import static com.mibs.mars.utils.MUtils.regDate;
import static com.mibs.mars.utils.Messages.ERROR_EXPLORATION_SAVE;
import static com.mibs.mars.utils.Messages.SUCCESS_EXPLORATION_SAVE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import com.mibs.mars.config.AppConfig;
import com.mibs.mars.dao.ExplorationNew;
import com.mibs.mars.entity.Conclusion;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.Payments;
import com.mibs.mars.entity.RemotePaths;

import com.mibs.mars.entity.Users;
import com.mibs.mars.exception.ErrorCreateProfileException;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exception.FolderNotFoundException;
import com.mibs.mars.exceptions.CabinetBuildException;
import com.mibs.mars.exceptions.ErrorTransferDICOMException;
import com.mibs.mars.exceptions.TransferDicomException;
import com.mibs.mars.net.MailAgent;
import com.mibs.mars.repository.ConclusionRepository;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ImagesRepository;
import com.mibs.mars.repository.InvitationsRepository;
import com.mibs.mars.repository.JournalRepository;
import com.mibs.mars.repository.PaymentsRepository;
import com.mibs.mars.repository.RemotePathsRepository;
import com.mibs.mars.repository.UsersRepository;
import com.mibs.mars.service.ExplorationUniqueName;
import com.mibs.mars.utils.Dcm2Img;
import com.mibs.mars.utils.DicomHandler;
import com.mibs.mars.xml.XMLRequest;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

abstract class AbstractController {
	static Logger logger = LoggerFactory.getLogger(AbstractController.class);
	//private Locale locale = Locale.getDefault();
	
	protected ExplorationUniqueName explorationUniqueName;
	@Autowired 
	private UsersRepository usersRepository;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private ImagesRepository imagesRepository;
	@Autowired
	private ConclusionRepository conclusionRepository;
	@Autowired 
	private ExplorationRepository explorationRepository;
	@Autowired
	protected RemotePathsRepository remotePathsRepository;
	@Autowired
	protected JournalRepository journalRepository;
	@Autowired
	protected PaymentsRepository paymentsRepository;
	@Autowired
	protected InvitationsRepository invitationsRepository;
	@Autowired
	protected MessageSource messageSource;
	
	
	protected void deleteDir(Path path) {
		try {
			Files.walk(path)
			  .sorted(Comparator.reverseOrder())
			  .map(Path::toFile)
			  .forEach(File::delete);
		} catch (IOException e) {
			logger.error("Error while deleting directory: " + path.getFileName());
		}
	}
	
	private String createLocalStorageDir(String dicomName) throws FolderNotFoundException {
		String result = appConfig.getStoragePath() + "/" + dicomName;
		File destDir = new File( result );
		if (!destDir.exists()) {
			if (!destDir.mkdir()) throw new FolderNotFoundException("Error: directory not created with path: " + appConfig.getStoragePath());
		}
		return result;
	}
/*	protected void buildCabinet(Exploration exploration, String login, String password) throws CabinetBuildException {
		try {
			String localPath = createLocalStorageDir( exploration.getDicomname() );
			try {
				transferDicom(exploration.getRemotepath(), localPath, login, password);
				Dcm2Img dcm2Img = new Dcm2Img();
				dcm2Img.initImageWriter("JPEG","jpeg", null, null,null);
				String serPath = appConfig.getSerializedPath() + "/" + exploration.getDicomname();
				try {
					dcm2Img.CCCatch(localPath, serPath, exploration, imagesRepository);
					long size = pack(localPath, localPath + "/" + exploration.getDicomname() + ".zip");
					logger.info("Update Exploration with new dicom size: "  + size);
					explorationRepository.updateDicomSize( size , exploration.getId() );
					logger.info("Congratulation! Patients Private Cabinet is fully created!");
				} catch (ErrorDicomParsingException e) {
					throw new CabinetBuildException("Error building cabinet with massege: "+ e.getMessage());
				}
			}catch(TransferDicomException e) {
				throw new CabinetBuildException("TransferDicom Exception with massege: "+ e.getMessage());
			}
		
			
		} catch (FolderNotFoundException e) {
			throw new CabinetBuildException("Error building cabinet with massege: "+ e.getMessage());
		}
		
		
	}
	*/
	protected QueryResult buildCabinet2(ExplorationNew exp,  Exploration saved  ) {
		
		 String remoteSmbPath = "smb://"  +exp.getUsername() + ":" + exp.getPassword() + "@" + exp.getPath();
		 //   String remoteSmbPath = "smb://admin:admin@172.16.30.107/Public/CABTEST/";
		    int transfered = 0;
		    int parsed = 0;
		    try {
				SmbFile sfile =  new SmbFile( remoteSmbPath );
				 try {
					DicomHandler handler = new DicomHandler(saved.getDicomname(), appConfig.getSerializedPath(), appConfig.getStoragePath());
					try {
						transfered = handler.transferDICOMFiles(sfile.listFiles()) ;
						if (transfered > 0) {
							parsed = handler.parsingDICOMFiles(saved.getId(), imagesRepository);
							if (parsed == 0) {
								 logger.error("Error transfering files. There is nothing to be parsed.");
								 //explorationRepository.delete(saved);
								 return new QueryResult(ERROR_EXPLORATION_SAVE);	
							}else {
								String path = appConfig.getStoragePath() + "/" + saved.getDicomname();
								long size = pack( path, path + "/" + saved.getDicomname() + ".zip");
								explorationRepository.updateDicomSize( size , saved.getId() );
							}
						}else {
							 logger.error("Error transfering files. There is nothing to be parsed.");
							 //explorationRepository.delete(saved);
							 return new QueryResult(ERROR_EXPLORATION_SAVE);
						}
					} catch (ErrorTransferDICOMException e) {
						 logger.error("Error Transfering  DICOM " + e.getMessage() );
						 //explorationRepository.delete(saved);
						 return new QueryResult(ERROR_EXPLORATION_SAVE);
					}
					
				 } catch (SmbException e) {
					 logger.error("Error SmbException " + e.getMessage() );
					 //explorationRepository.delete(saved);
					 return new QueryResult(ERROR_EXPLORATION_SAVE);
					 
				}
		    } catch (MalformedURLException e) {
		    	logger.error("Error Malformed URL Exception " + e.getMessage() );
		    	//explorationRepository.delete(saved);
				return new QueryResult(ERROR_EXPLORATION_SAVE);
			}
			return new QueryResult(SUCCESS_EXPLORATION_SAVE);

	}

/*	
	private void transferDicom(String remotePath, String localPath, String t_login, String t_password) throws TransferDicomException  {
		
		//jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords","false");
		
		String s1 = remotePath.replaceAll("\\\\", "/");
		Path path = Paths.get( s1 );
		String ipAddress = path.getName(0).toString();
		String dirName = path.getName(1).toString().toLowerCase();
		RemotePaths remotePaths = remotePathsRepository.findByIpaddressAndDirnameIgnoreCase(ipAddress, dirName);
		
		if (remotePaths == null) {
			
			throw new TransferDicomException("Error: Remote path is not found for ip ["  + ipAddress + "] and directory [ " + dirName + "]");
		
		}
		
		String login = (t_login == null) ? remotePaths.getLogin() : t_login; 
		String password =(t_password == null) ?  remotePaths.getPasswd() : t_password;
		
		String smbDirName = "smb://" + login + ":" + password +"@" + s1.substring(2);
		
		String smbDirNameInfo = "smb://" + login.substring(0,2) + "***" + login.substring(login.length()-1, login.length()) + 
				":" + password.substring(0,2) + "****" + password.substring(password.length()-1, password.length()) +"@" + s1.substring(2);
		
		logger.info("Start extracting files from: " + smbDirNameInfo);
		
		SmbFile sfile;
		try {
			sfile = new SmbFile( smbDirName );
			SmbFile[] files;
			try {
				files = sfile.listFiles();
				for(SmbFile f : files) {
					SmbFile dstfiles = new SmbFile( smbDirName + f.getName() );
					InputStream input = null;
					OutputStream output = null;
					try {
						input = dstfiles.getInputStream();
						output = new FileOutputStream(localPath + "/" + f.getName() );
						IOUtils.copy(input, output);
						logger.info("Copying " + " to: " +  f.getName());
					} catch (IOException e) {
						logger.error("IOException with massege: "+ e.getMessage());
						throw new TransferDicomException("IOException with massege: "+ e.getMessage());
					}finally {
						try {
							if (input!=null) input.close();
							if (output != null) output.close();
						} catch (IOException e) {
							logger.error("IOException with massege: "+ e.getMessage());
							throw new TransferDicomException("IOException with massege: "+ e.getMessage());
						}
						
					}
				
				}
			} catch (SmbException e) {
				logger.error("Smb Exception with massege: "+ e.getMessage());
				throw new TransferDicomException("Smb Exception with massege: "+ e.getMessage());
		
			} catch (Exception e) {
				logger.error("Exception with massege: "+ e.getMessage());
				throw new TransferDicomException("Smb Exception with massege: "+ e.getMessage());
			}
		
		} catch (MalformedURLException e) {
			logger.error("MalformedURL Exception with massege: "+ e.getMessage());
			throw new TransferDicomException("MalformedURL Exception with massege: "+ e.getMessage());
		}
		logger.info("Stop extracting files from: " + smbDirNameInfo);
		
	}
*/
	protected long pack(String sourceDirPath, String zipFilePath) {
		logger.info("Start creating ZIP file: "  + zipFilePath);
		long result = 0;
		try {
			try {
				Files.deleteIfExists(Paths.get(zipFilePath));
			
			} catch (IOException e1) {

				logger.error("Error delete If Exists for " + zipFilePath + " with message " + e1.getMessage());
			}
			
			ZipFile zipFile = new ZipFile(zipFilePath);
			ArrayList<File> fs = new ArrayList<>();
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setRootFolderInZip("Documents/");
			Path pp = Paths.get(sourceDirPath);
		    try {
				Files.walk(pp)
				      .filter(path -> !Files.isDirectory(path))
				      .forEach(path -> {
				    	  fs.add( new File(path.toString()));
				      });
			} catch (IOException e) {
				logger.error("Error while adding files to zip list with native message: " + e.getMessage());
			}
		    zipFile.addFiles(fs, parameters);
		    result = zipFile.getFile().length();
		    fs.forEach(f->{
		    	try {
					Files.delete( f.toPath() );
				} catch (IOException e) {
					logger.error("Error while deleting files from zip list with native message: " + e.getMessage());
				}
		    });
		} catch (ZipException e) {
			logger.error("Error while creating Zip object with native message: " + e.getMessage());
		}
		logger.info("ZIP file is created: "  + zipFilePath);
		return result;
		
	}
	

	
	protected void addConclusions(MultipartFile[] multipartFile, Exploration exploration) {
		List<Conclusion> Cons =  conclusionRepository.findByExplorationid( exploration.getId());
		if (Cons.size() > 0) {
			for(Conclusion co : Cons) {
				conclusionRepository.delete(co);
			}
		}
		for(int i=0; i < multipartFile.length; i++) {
			Conclusion conclusion = new Conclusion();
			conclusion.setExplorationid( exploration.getId() );
			byte[] byteArray;
			try {
				byteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(new String(multipartFile[i].getBytes()));
				conclusion.setConclusionfile( byteArray );
				conclusion.setFilename(Paths.get( multipartFile[i].getOriginalFilename() ).getFileName().toString());
				conclusionRepository.save( conclusion );
			} catch (Exception e) {
				logger.error("Error saving conclusion with message: " + e.getMessage());
			}
		}
	}
	protected void addConclusions(MultipartFile multipartFile, Exploration exploration) {
		Conclusion conclusion = new Conclusion();
		conclusion.setExplorationid( exploration.getId() );
		try {
			conclusion.setConclusionfile( multipartFile.getBytes() );
			conclusion.setFilename(Paths.get( multipartFile.getOriginalFilename() ).getFileName().toString());
			conclusionRepository.save( conclusion );
		} catch (Exception e) {
			logger.error("Error saving conclusion with message: " + e.getMessage());
		}
	}
	
	protected Exploration addExploration(ExplorationUniqueName unique, Users users,  String explorationName) {
		
		Exploration exploration = new Exploration();
		exploration.setUsersId( users.getId());
		exploration.setDate(regDate());
		exploration.setUniqueid( unique.getUniqueName() );
		exploration.setExplname( explorationName );
		exploration.setDicomname( unique.getUniqueName() );
		explorationRepository.save(exploration);
		return exploration;
		
	}
	protected String copyFilesToLocalDir(String username, String password, String pathStr, ExplorationUniqueName unique) throws FolderNotFoundException, IOException {
		
		String smbDirName = "smb://" + username + ":" + password +"@" + pathStr;
		SmbFile  sfile = new SmbFile( smbDirName );
		SmbFile[] files = sfile.listFiles();
		String dstPath = appConfig.getStoragePath() + "/" +  unique.getUniqueName();
		File destDir = new File( dstPath );
		if (!destDir.exists()) {
			if (!destDir.mkdir()) throw new FolderNotFoundException("Directory not created");
		}
		for(SmbFile f : files) {
			SmbFile dstfiles = new SmbFile( smbDirName + f.getName() );
			InputStream input = dstfiles.getInputStream();
			OutputStream output = new FileOutputStream(dstPath + "/" + f.getName() );
			IOUtils.copy(input, output);
			input.close();
			output.close();
		}
		
		return dstPath;
		
	}
	private String copyFilesToLocalDir(String path, String uid2dir) throws FolderNotFoundException, IOException {
		String s1 = path.replaceAll("\\\\", "/");
		Path s2 = Paths.get(s1);
		String ipAddress = s2.getName(0).toString();
		String dirName = s2.getName(1).toString();
		RemotePaths remotePaths = remotePathsRepository.findByIpaddressAndDirname(ipAddress, dirName);
		String login =remotePaths.getLogin(); 
		String password = remotePaths.getPasswd();
		String smbDirName = "smb://" + login + ":" + password +"@" + s1.substring(2);
		SmbFile  sfile = new SmbFile( smbDirName );
		SmbFile[] files = sfile.listFiles();
		//String dstPath = appConfig.getStoragePath() + "/" + s2.getName(s2.getNameCount() - 1);
		String dstPath = appConfig.getStoragePath() + "/" + explorationUniqueName.getUniqueName();

		//String dstPath = appConfig.getStoragePath() + "/" + uid2dir ;
		File destDir = new File( dstPath );
		if (!destDir.exists()) {
			if (!destDir.mkdir()) throw new FolderNotFoundException("Directory not created");
		}
		for(SmbFile f : files) {
			SmbFile dstfiles = new SmbFile( smbDirName + f.getName() );
			InputStream input = dstfiles.getInputStream();
			OutputStream output = new FileOutputStream(dstPath + "/" + f.getName() );
			IOUtils.copy(input, output);
			input.close();
			output.close();
		}
		//return appConfig.getSerializedPath() + "/" + s2.getName(s2.getNameCount() - 1);
		return dstPath;
	}
	
	
}
