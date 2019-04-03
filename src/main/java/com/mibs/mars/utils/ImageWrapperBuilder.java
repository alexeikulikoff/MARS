package com.mibs.mars.utils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mibs.mars.config.AppConfig;
import com.mibs.mars.controllers.MarsController;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.ImageEntity;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ImagesRepository;

public class ImageWrapperBuilder {

	static Logger logger = LoggerFactory.getLogger(ImageWrapperBuilder.class);
	private ImagesRepository imagesRepository;
	private ExplorationRepository explorationRepository;
	private AppConfig appConfig;;
	public ImageWrapperBuilder(ImagesRepository  imageRepo, ExplorationRepository explorationRepo, AppConfig conf) {
	
		imagesRepository = imageRepo;
		explorationRepository = explorationRepo;
		appConfig = conf;
	}
	public DicomImage buildDicomImage(Long id) {
		Set<Long> indexes = new TreeSet<>();
		Exploration expl = explorationRepository.findById(id);
		DicomImage dicomImage = new DicomImage( expl.getExplname() );
		String uid2dir = expl.getDicomname();
		String path = appConfig.getSerializedPath() + "/" + uid2dir;
		List<ImageEntity> images = imagesRepository.findByExplorationidOrderBySerialAsc(id);
		images.forEach( im -> {
			indexes.add(  im.getSerial() );
		});
		indexes.forEach(in-> {
			Series series = new Series( in );
			long ins = 0;
			for(ImageEntity ie : images) {
				if ( in.longValue() == ie.getSerial() ){
					SerializedDicom serializedDicom = null;
					byte[] binArr = null;
					String serializedFile = path + "/data-" + ie.getSerial() + "-" + ie.getInst() + ".ser";
					try {
						FileInputStream fs = new FileInputStream(serializedFile);
						ObjectInputStream objInp = new ObjectInputStream(fs);
						serializedDicom = (SerializedDicom) objInp.readObject();
				        objInp.close();
				        fs.close();
						/*try {
							binArr = IOUtils.toByteArray(fs);
						} catch (IOException e) {
							logger.error("Error converting to byte array for file :" + serializedFile);
						}
*/						
					} catch (ClassNotFoundException | IOException e) {
						logger.error("Error open file :" + serializedFile);
					}
				
					//Instance instance = new Instance(im.getInst(), binArr, serializedDicom);
				
					ins++;
					serializedDicom.setInstance((int)ins);
					Instance instance = new Instance(ins, binArr, serializedDicom);
					series.addInstance( instance );
					
				}
			}
			dicomImage.addSeries( series );
		});
/*			
			images.forEach( im ->{
				if ( in.longValue() == im.getSerial()) {
					SerializedDicom serializedDicom = null;
					byte[] binArr = null;
					String serializedFile = path + "/data-" + im.getSerial() + "-" + im.getInst() + ".ser";
					try {
						FileInputStream fs = new FileInputStream(serializedFile);
						ObjectInputStream objInp = new ObjectInputStream(fs);
						serializedDicom = (SerializedDicom) objInp.readObject();
				        objInp.close();
				        fs.close();
						
					} catch (ClassNotFoundException | IOException e) {
						logger.error("Error open file :" + serializedFile);
					}
				
					//Instance instance = new Instance(im.getInst(), binArr, serializedDicom);
					Instance instance = new Instance(ins, binArr, serializedDicom);
					series.addInstance( instance );
				}
			});
			
			dicomImage.addSeries( series );
		});
*/		
		return dicomImage;
	}
}
