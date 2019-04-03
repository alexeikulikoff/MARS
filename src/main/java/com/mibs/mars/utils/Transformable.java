package com.mibs.mars.utils;


import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exceptions.ErrorTransferDICOMException;
import com.mibs.mars.repository.ImagesRepository;

import jcifs.smb.SmbFile;

public interface Transformable {

	int transferDICOMFiles(SmbFile[] files) throws ErrorTransferDICOMException;
		
	int parsingDICOMFiles(Long explorationid,ImagesRepository imagesRepository) throws ErrorDicomParsingException;	
	
	void clearImageTable(Long explorationid, ImagesRepository imagesRepository);
	void saveImageEntity(Long id, Long instance, Long seria,ImagesRepository imagesRepository) throws Exception;
	
	void execute();
}
