package com.mibs.mars.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.image.BufferedImageUtils;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.dcm4che3.io.SAXWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;

import com.mibs.mars.controllers.ConfigController;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.ImageEntity;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.repository.ImagesRepository;
import com.mortennobel.imagescaling.ResampleOp;

public class Dcm2Img {
	 static Logger logger = LoggerFactory.getLogger(Dcm2Img.class);
	 private int frame = 1;
	 private String suffix;
	 private ImageWriter imageWriter;
	 private ImageWriteParam imageWriteParam;
	 private static ResourceBundle rb = ResourceBundle.getBundle("messages");
	 private final ImageReader imageReader;

	 public void initImageWriter(String formatName, String suffix, String clazz, String compressionType, Number quality) {
	        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(formatName);
	        if (!imageWriters.hasNext()) throw new IllegalArgumentException( MessageFormat.format(rb.getString("formatNotSupported"), formatName));
	        this.suffix = suffix != null ? suffix : formatName.toLowerCase();
	        imageWriter = imageWriters.next();
	        if (clazz != null)
	            while (!clazz.equals(imageWriter.getClass().getName()))
	                if (imageWriters.hasNext())
	                    imageWriter = imageWriters.next();
	                else
	                    throw new IllegalArgumentException( MessageFormat.format(rb.getString("noSuchImageWriter"), clazz, formatName));
	        imageWriteParam = imageWriter.getDefaultWriteParam();
	        if (compressionType != null || quality != null) {
	            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	            if (compressionType != null)
	                imageWriteParam.setCompressionType(compressionType);
	            if (quality != null)
	                imageWriteParam.setCompressionQuality(quality.floatValue());
	        }
	}
	 private BufferedImage readImage(ImageInputStream iis) throws IOException {
	        imageReader.setInput(iis);
	        return imageReader.read(frame-1, readParam());
	}
	 private ImageReadParam readParam() {
	        DicomImageReadParam param = (DicomImageReadParam) imageReader.getDefaultReadParam();
	        //param.setWindowWidth(512);
	        param.setAutoWindowing(true);
	        
	        return param;
	 }
	 private BufferedImage convert(BufferedImage bi) {
	        ColorModel cm = bi.getColorModel();
	        return cm.getNumComponents() == 3 ? BufferedImageUtils.convertToIntRGB(bi) : bi;
	    }

	 private void writeImage(ImageOutputStream ios, BufferedImage bi) throws IOException {
	        imageWriter.setOutput(ios);
	        imageWriter.write(null, new IIOImage(bi, null, null), imageWriteParam);
	    }


	private String suffix(File src) {
	        return src.getName() + '.' + suffix;
	}

	 public void convert(File src, File dest) throws IOException {
	        ImageInputStream iis = ImageIO.createImageInputStream(src);
	        try {
	            BufferedImage bi = readImage(iis);
	            bi = convert(bi);
	            dest.delete();
	            ImageOutputStream ios = ImageIO.createImageOutputStream(dest);
	            try {
	                writeImage(ios, bi);
	            } finally {
	                try { ios.close(); } catch (IOException ignore) {}
	            }
	        } finally {
	            try { iis.close(); } catch (IOException ignore) {}
	        }
	    }
	   private void mconvert(File src, File dest) {
	        if (src.isDirectory()) {
	            dest.mkdir();
	            for (File file : src.listFiles())
	                mconvert(file, new File(dest, 
	                        file.isFile() ? suffix(file) : file.getName()));
	            return;
	        }
	        if (dest.isDirectory())
	            dest = new File(dest, suffix(src));
	        try {
	            convert(src, dest);
	            logger.info( "Converted " +  src + "->" +  dest );
	            //System.out.println( MessageFormat.format(rb.getString("converted"), src, dest));
	        } catch (Exception e) {
	        	logger.error( "Fail to convert " +  src + "->" +  dest );
	            //System.out.println( MessageFormat.format(rb.getString("failed"), src, e.getMessage()));
	            //e.printStackTrace(System.out);
	        }
	    }

	public Dcm2Img() {
		 ImageIO.scanForPlugins();            
		 imageReader = ImageIO.getImageReadersByFormatName("DICOM").next();
	}

	public void CCCatch(String dicomFolderName, String serPath,  Exploration exploration, ImagesRepository imagesRepository) throws ErrorDicomParsingException {
		byte[] data = null;
		List<ImageEntity> imgs = imagesRepository.findByExplorationid( exploration.getId());
		if (imgs.size() > 0) {
			for(ImageEntity im : imgs) {
				imagesRepository.delete(im);
			}
		}
		File dstfile = new File(serPath);
		 if (!dstfile.exists()) {
	            if (dstfile.mkdir()) {
	               logger.info("Directory for Serialized objects is created. Directory name: " + serPath);
	            } else {
	            	logger.error("Failed to create directory for Serialized objects. Directory name: " + serPath );
	            }
	        }
		File file = new File(dicomFolderName);
		File[] listFiles = file.listFiles();
		for(File f : listFiles) {
			DicomInputStream dis = null;
			try {
				dis = new DicomInputStream( f );
				Attributes attrs = dis.readDataset(-1, -1);
				Integer instance= attrs.getInt(Tag.InstanceNumber, 0);
				Integer seria= attrs.getInt(Tag.SeriesNumber, 0);
				String s = serPath + "/" + seria + "-" + instance + "-img";
				String s0 = s + ".jpg";
				File dest = new File(s0);
				mconvert(f, dest);
				
			    BufferedImage bImage = ImageIO.read( dest );
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
	            ImageIO.write(bImage, "jpg", bos );
	            bos.flush();
	            data = bos.toByteArray();
	            bos.close();
				//dest.delete();
				
				SerializedDicom sdic = new SerializedDicom();
				
				sdic.setImage(data );
			
				sdic.setSeria( attrs.getInt(Tag.SeriesNumber, 0) );
				sdic.setInstance( attrs.getInt(Tag.InstanceNumber, 0) );
				
				sdic.setPatientName(attrs.getString(Tag.PatientName));
				sdic.setPatientBirthDate(attrs.getString(Tag.PatientBirthDate));
				sdic.setPatientID(attrs.getString(Tag.PatientID));
				sdic.setPatientAge(attrs.getString(Tag.PatientAge));
				sdic.setPatientWeight(attrs.getString(Tag.PatientWeight));
				sdic.setPatientSex(attrs.getString(Tag.PatientSex));
				sdic.setInstitutionName(attrs.getString(Tag.InstitutionName));
				sdic.setStudyDate(attrs.getString(Tag.StudyDate));
				sdic.setStudyComment(attrs.getString(Tag.StudyComments));
				sdic.setStudyDescription(attrs.getString(Tag.StudyDescription));
				sdic.setSliceThickness(attrs.getString(Tag.SliceThickness));
				sdic.setPerformingPhysicianName(attrs.getString(Tag.PerformingPhysicianName));
				sdic.setSliceLocation(attrs.getString(Tag.SliceLocation));
				sdic.setRows(attrs.getInt(Tag.Rows, 0));
				sdic.setColumns(attrs.getInt(Tag.Columns, 0));
				sdic.setPixelSpacing(attrs.getString(Tag.PixelSpacing));
				sdic.setManufactoreModelName(attrs.getString(Tag.ManufacturerModelName));
				
				String serializedFile = serPath + "/data-" + seria + "-" + instance + ".ser";
				FileOutputStream fileOut = new FileOutputStream(serializedFile);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject( sdic );
				out.close();
				fileOut.close();
				//logger.info("Write parsed DICOM file to: " + serializedFile);
				ImageEntity imageEntity = new ImageEntity();
				imageEntity.setExplorationid(exploration.getId());
				imageEntity.setInst(new Long(instance));
				imageEntity.setSerial(new Long(seria));
		
				imagesRepository.save(imageEntity);
			} catch (Exception e) {
				logger.error("Error while parsing DICOM with mesage: " + e.getMessage());
				throw new ErrorDicomParsingException("Error parsing DICOM");
			}finally {
				if (dis != null) {
					try {
						dis.close();	
					} catch (IOException e) {
						throw new ErrorDicomParsingException("Error parsing DICOM");
					}
				}
			}
		}
	 logger.info("DICOM parsing is done with success! ");
	}
	
}
