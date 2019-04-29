package com.mibs.mars.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mibs.mars.entity.ImageEntity;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exceptions.ErrorTransferDICOMException;
import com.mibs.mars.repository.ImagesRepository;

import jcifs.smb.SmbFile;


public class DicomHandler extends AbstractDicomHandler implements Transformable {
	
	static Logger logger = LoggerFactory.getLogger(DicomHandler.class);
	private int counter = 0;

	public DicomHandler(String dicomName, String serializePath, String dicomPath ) {
		super(dicomName,serializePath,dicomPath);
	}
	 public DicomHandler( String dicomName, String serializePath, String dicomPath, String remoteSmbPath ) {
		 super(dicomName,serializePath,dicomPath,remoteSmbPath);
	 }
	private int copyFiles(String path, SmbFile[] files, boolean recursion) throws IOException, InterruptedException {
		if ((files != null) && (files.length > 0) ) {
			for (SmbFile file : files) {
				if (file.isDirectory()) {
					recursion = true;
					copyFiles(path, file.listFiles(),recursion);
				} else {
					
					SmbFile src = new SmbFile(file.getCanonicalPath());
					
					InputStream input = src.getInputStream();
					String dstFileName = (recursion) ?  path + "/DCM-" + counter + "-"  + src.getName() : path + "/" + src.getName();
					OutputStream output = new FileOutputStream(dstFileName);
					IOUtils.copy(input, output);
					logger.info("Copy file " + src.getName()  + " ->  " + dstFileName );
					output.close();
					input.close();
					counter++;
				}
			}	
		}
		return counter;
	}

	@Override
	public int transferDICOMFiles(SmbFile[] files) throws ErrorTransferDICOMException {
		int result = 0;
		ExecutorService service = null;
		try {
			service = Executors.newSingleThreadExecutor();
			Future<Integer> future = service.submit(() -> copyFiles(createLocalDir(dicomPath, dicomName), files, false));
			//result = future.get(timeout, TimeUnit.MINUTES);
			result = future.get();
		} catch (Exception e) {
			throw new ErrorTransferDICOMException("Error while transfering dicom files from remote!");

		} finally {
			if (service != null)
				service.shutdown();
		}
		return result;
	}

	@Override
	public int parsingDICOMFiles(Long explorationid,ImagesRepository imagesRepository){
		int result = 0;
		ExecutorService service = null;
		try {
			service = Executors.newSingleThreadExecutor();
			Future<Integer> future = service.submit(() -> {
				int counter = 0;
				clearImageTable(explorationid,imagesRepository);
				try {
					createLocalDir(serializePath, dicomName);
				} catch (FileNotFoundException e1) {
					logger.error("Error creating Local dir with message: " + e1.getMessage() );
				}
				String s = dicomPath + "/" + dicomName; 
				File[] dicoms = new File(s).listFiles();
				if ((dicoms != null) && (dicoms.length > 0) ){
					for (File dicom : dicoms) {
						DicomInputStream dis = null;
						try {
							dis = new DicomInputStream(dicom);
							Attributes attrs = dis.readDataset(-1, -1);
							Integer instance = attrs.getInt(Tag.InstanceNumber, 0);
							Integer seria = attrs.getInt(Tag.SeriesNumber, 0);
							String imgFileName = serializePath + "/" + dicomName + "/" + seria + "-" + instance + "-img.jpg";
							File imageFile = new File(imgFileName);
							byte[] data = convert(dicom, imageFile);
							if (data != null) {
								createSerializedDicom(attrs, data);
								try {
									saveImageEntity(explorationid, new Long(instance), new Long(seria),imagesRepository);
									counter++;
								} catch (Exception e) {
									logger.error("Error while saving image data : " + e.getMessage() + " for exploration id: " + explorationid);
								}
							}else {
								logger.error("Error createing Serialized Dicom for " + serializePath);
							}
						} catch (IOException e) {
							logger.error("Error reading temporary image file: " + e.getMessage());
							continue;
						} finally {
							if (dis != null) {
								try {
									dis.close();
								} catch (IOException e) {
								   logger.error("Error while handling DicomInputStream : " + e.getMessage() );
									continue;
								}
							}
						}
					}
				}else {
					logger.error("Error files to be parsed are not found for path " +  s);	
				}
				return counter;
			});
			
			
			result = future.get();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			 logger.error("Error while handling Future Thread : " + e.getMessage() );
		} finally {
			if (service != null)
				service.shutdown();
		}
		return result;

	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearImageTable(Long explorationid, ImagesRepository imagesRepository) {
		
		List<ImageEntity> imgs = imagesRepository.findByExplorationid( explorationid );
		if ((imgs != null ) && ( imgs.size() > 0)) {
			for(ImageEntity im : imgs) {
				imagesRepository.delete(im);
			}
		}
		
	}

	@Override
	public void saveImageEntity(Long id, Long instance, Long seria,ImagesRepository imagesRepository) throws Exception {
		ImageEntity imageEntity = new ImageEntity();
		imageEntity.setExplorationid(id);
		imageEntity.setInst(instance);
		imageEntity.setSerial(seria);
		imagesRepository.save(imageEntity);
	}

	public String getGreeting() {
		return "test message";
	}

}
