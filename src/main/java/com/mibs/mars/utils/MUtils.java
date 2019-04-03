package com.mibs.mars.utils;

import static com.mibs.mars.xml.XMLUtils.CODE;
import static com.mibs.mars.xml.XMLUtils.CONNECTIONSTRING;
import static com.mibs.mars.xml.XMLUtils.DATA;
import static com.mibs.mars.xml.XMLUtils.DATE;
import static com.mibs.mars.xml.XMLUtils.EMAIL;
import static com.mibs.mars.xml.XMLUtils.FAMILY;
import static com.mibs.mars.xml.XMLUtils.FIRST;
import static com.mibs.mars.xml.XMLUtils.ISPAID;
import static com.mibs.mars.xml.XMLUtils.PARENT;
import static com.mibs.mars.xml.XMLUtils.PATH;
import static com.mibs.mars.xml.XMLUtils.SERVICEADDRESS;
import static com.mibs.mars.xml.XMLUtils.STUDYNAME;
import static com.mibs.mars.xml.XMLUtils.SUMUNIT;
import static com.mibs.mars.xml.XMLUtils.UID;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mibs.mars.controllers.MarsController;
import com.mibs.mars.exception.FolderNotFoundException;
import com.mibs.mars.exceptions.CreateXMLException;
import com.mibs.mars.xml.XMLRequest;
import com.mortennobel.imagescaling.ResampleOp;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MUtils {
	
	static Logger logger = LoggerFactory.getLogger(MUtils.class);
	//public final static String HTTP200 		= "HTTP/1.1 200";
	public final static String HTTP200 		= "message";
	public final static String HTTP300 		= "HTTP/1.1 300";
	public final static String HTTP400 		= "HTTP/1.1 400";
	public final static String HTTP401 		= "HTTP/1.1 401";
	public final static String HTTP403 		= "HTTP/1.1 403";
	public final static String HTTP404 		= "HTTP/1.1 405";
	
	public final static String _CSRT 		= "_csrf";
	public final static String COOKIE 		= "Set-Cookie:";
	public final static String SEMICOLON	= ";";
	public final static String EQUAL 		= "=";
	public final static String BACKSLASH 	= "\"";
	public final static String DT			="Date";
	public final static String HT			="</html>";
	public final static String COMMA		=",";
	public final static String TOKEN		="token";
	public final static String COLON		=":";
	public final static String BOUNDARY	="---------------------------180028142619792095291342030079";
	public final static String REQUEST_XML = "request.xml";
	
	
	public static final String DATA 			= "data";
	public static final String CONNECTIONSTRING = "connectionString";
	public static final String SERVICEADDRESS 	= "serviceAddress";
	public static final String EMAIL 			= "email";
	public static final String FIRST			= "first";
	public static final String PARENT 			= "parent";
	public static final String FAMILY 			= "family";
	public static final String UID 				= "uid";
	public static final String STUDYNAME 		= "studyName";
	public static final String PATH 			= "path";
	public static final String ISPAID 			= "isPaid";
	public static final String CODE 			= "code";
	public static final String COUNT 			= "count";
	public static final String SUMUNIT 			= "sumUnit";
	public static final String DATE 			= "date";
	
	public static final String ERROR 			= "Error";
	public static final String MESSAGE 			= "Message";
	public static final int	  AYEAR				= 31536000;
	
	//
	public static final String empty_image="/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wgARCADmAOYDASIAAhEBAxEB/8QAGgABAQADAQEAAAAAAAAAAAAAAAQBAwUCBv/EABQBAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhADEAAAAfuQAAAAAAAAAAAAAAAAAAAAGNRuT5N7GQAAAAAAAAAABP6gM4ADNcY6qegAAAAAAAAAGsh8AAAB66fKvNwAAAAAAAAE9EhKAAABXJSWAAAAAAAAASVykgAAAFE9RWAAAAAAAABo3+TmAAAAWR9I9gAAAAAAAAA5+q6EAAA2dGegAAAAAAAAAAxy+rzjWAAejo+gAAAAAAAAAGDPOpjMAAevOTqNO4AAAAAAAGo2+Y9BVP5AAAAD34FlHLydRHSewAACYonkwbNYAAAAAAAAAbqIR1XMuNoNXP26gAAAAAAAAAAABnA6aYSgAAAAAAAAAAAAAywAAAAAAAAAAAAAAAP/xAAiEAABBAICAgMBAAAAAAAAAAABAgMRQAAwIDITMRIhYBD/2gAIAQEAAQUC/RkxnkTnlGeROAzYW5GEzxQ5WcVA5tKmqsyrmkwabhhGloyik910sU39THuk/qZ7Un9TFN7rpZ9UlCU6UCE03BCuaBKqjolPNkfVQ6AIFVffintWc78U9qqnRhMniPopcBpEgYp3CSdQURiXc97VLAxThO8GMS7gUDoJjC6MKyagcIwOA8XHIsgkYhfy/jiviLQMEGQ6ZVbZP5j/xAAUEQEAAAAAAAAAAAAAAAAAAABw/9oACAEDAQE/AWH/xAAUEQEAAAAAAAAAAAAAAAAAAABw/9oACAECAQE/AWH/xAAiEAABBAIBBAMAAAAAAAAAAAABACEwQBEgUBBRYGECMUH/2gAIAQEABj8C8jfp+9GsYH2n1x8q3uDBu5qHhzTERpiI0xEeQMQ5nPJnYVzsKzQdqTpk8TJ52adk6aB01btrgWWXu9m6R4x//8QAJBABAAEDBAMAAgMAAAAAAAAAAREAMUAhMEFRIGGhcYEQUGD/2gAIAQEAAT8h/wBGA1RSXK/ivXQbsoLg5NSKVPgKMjDU3Z3jacXbEbkMWWdiDHFGpOaCA9aYb0HvaWgzX4MO7a/Jh2bW7DCZ9O0db24cA2oliT/TrsR5xzi67ybET7YokR5pIU84k6xvt8vvMf6fL7zFUCXSgd6kF58lA9VctXvCvqK6P7aV1TtX9+qBtihBIzu8vL6rpClm+8ilRScZ9lXtsBcCiWzV7dOjE90e6v2qjW3hI5e6VWVnId1UQ6/habtstiS9EQ5qR6NMy+fmnXMFLf3f/9oADAMBAAIAAwAAABDzzzzzzzzzzzzzzzzzzzzzDTTzzzzzzzzzzyAAADTzzzzzzzzzgAAAATzzzzzzzzywAAABTzzzzzzzzygAAABDzzzzzzzzygAAABDzzzzzzzzzwgAABTzzzzzzzzzzgAATzzzzzzzzzzyQABDDzzzzzzzyyAAAAADTzzzzxAAAAAAAAAABBDwgAAAAAAAAAAABDyAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD/8QAFBEBAAAAAAAAAAAAAAAAAAAAcP/aAAgBAwEBPxBh/8QAFBEBAAAAAAAAAAAAAAAAAAAAcP/aAAgBAgEBPxBh/8QAKRABAAECBgEDBAMBAAAAAAAAAREAITAxQEFRYXEggcGRobHwEFDRYP/aAAgBAQABPxD/AKOSAOVrKPAVc2vas8fIf5Q8idOoJMDudipIFy+g0oNyp5AbOXnTXt4ejnASekrPJpeC5g8YC7kUgBcSTRyQzSD3wrxz0ZFypfbC8GR0byeX8YSt6N9k/GF+x3ozc4U/GEL/AABo/Gh+MKPrn00fNKW84XJkS+XSWGbDAtVZfw0tol2fbfAlQu7eDShloQ0zmYw+oFQM2iL2RrI/Zc61P2XOlZKA3WpEC+cimiUqWPVbeZDFQoujJ9aLltCJJ/lS5R/plUwVd4TN2OVyoIk8lyowhyOLJD0LqliHRdpEqVd3GkwXTQoIngaNsrw2cAKQO2rYj5bFWuPwDRlm1Wter/VRQ3sy+tIAoR3PRJRxz4U6UuV1E6Q62agbYzPk/j2VdO6bt9UgcCssgVDh2XzrM5dPn4pKVzb6x2VD/d//2Q==";
	

	public static String UniqueID() {
		return Instant.now().getEpochSecond() + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
	}
	public static String Password() {
		return UUID.randomUUID().toString().substring(0,8);
	}
	public static XMLRequest createXMLRequest( MultipartFile fls ) throws CreateXMLException {
		XMLRequest xmlRequest = new XMLRequest();
		try {
			byte[] byteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(new String(fls.getBytes()));
			ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(input);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName(DATA);
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					xmlRequest.setConnectionString(
							eElement.getElementsByTagName(CONNECTIONSTRING).item(0).getTextContent());
					xmlRequest.setServiceAddress(
							eElement.getElementsByTagName(SERVICEADDRESS).item(0).getTextContent());
					xmlRequest.setEmail(eElement.getElementsByTagName(EMAIL).item(0).getTextContent());
					xmlRequest.setFirst(eElement.getElementsByTagName(FIRST).item(0).getTextContent());
					xmlRequest.setParent(eElement.getElementsByTagName(PARENT).item(0).getTextContent());
					xmlRequest.setFamily(eElement.getElementsByTagName(FAMILY).item(0).getTextContent());
					xmlRequest.setUid(eElement.getElementsByTagName(UID).item(0).getTextContent());
					xmlRequest.setStudyName(eElement.getElementsByTagName(STUDYNAME).item(0).getTextContent());
					xmlRequest.setPath(eElement.getElementsByTagName(PATH).item(0).getTextContent());
					xmlRequest.setIsPaid(eElement.getElementsByTagName(ISPAID).item(0).getTextContent());
					xmlRequest.setCode(eElement.getElementsByTagName(CODE).item(0).getTextContent());
					xmlRequest.setSumUnit(eElement.getElementsByTagName(SUMUNIT).item(0).getTextContent());
					xmlRequest.setDate(eElement.getElementsByTagName(DATE).item(0).getTextContent());
					xmlRequest.setCount( eElement.getElementsByTagName(COUNT).item(0).getTextContent() );
				}
			}
			
		} catch (Exception e1) {
			logger.error("Error parsing XML file has occurred with message: " + e1.getMessage());
			throw new CreateXMLException("Error while handling XML request with message: " + e1.getMessage());
		}
		return xmlRequest;
	}
	
	public static String UnixTimeToStringDate(Long uniz) {
		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		Instant instant = Instant.ofEpochSecond(uniz,0);
		ZonedDateTime zd = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		return zd.format(fmt);
	}
	public static String UnixTimeToStringDateOnly(Long uniz) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd:MM:YYYY");
		Instant instant = Instant.ofEpochSecond(uniz,0);
		ZonedDateTime zd = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		return zd.format(fmt);
	}
	public static Long regDate(){
		return ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toEpochSecond();

	}
	public static boolean testRemouteStorage(String url, String login, String password) {
		boolean result = false;
		String uri = "smb://" + login+":" + password +"@" + url +"/";  ;
		try {
			SmbFile file = new SmbFile( uri );
			result = file.exists();
		}catch(Exception e) {
			logger.error("Resource " + uri + " is not reachale.");
		}
		return result;
	}
	public static String findFolderNamebyPatientId(String url, String login, String password, String id) throws SmbException, MalformedURLException, FolderNotFoundException {
		SmbFile file = new SmbFile("smb://" + login+":" + password +"@" + url +"/");
		SmbFile[] files = file.listFiles(s->{
				return id.equals(s.getName().substring(s.getName().lastIndexOf("_") + 1,s.getName().length()-1));
		});
		if (files.length == 0) 
			throw new FolderNotFoundException("Folder not found!");
		return files[0].getName().substring(0, files[0].getName().length()-1);
	}
	public static byte[] scaleImage(byte[] OriginalImage, int width, int height) {
		byte[] result = null;
		BufferedImage bufferedImage = null;
		InputStream in = new ByteArrayInputStream(OriginalImage);
		try {
			BufferedImage sourceImage = ImageIO.read(in);
			ResampleOp resampleOp = new ResampleOp(width,width);
			bufferedImage = resampleOp.filter(sourceImage,null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( bufferedImage, "jpg", baos );
			baos.flush();
			result = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			logger.error("Error while scaling image in serialized object with message: " + e.getMessage());
		}
		return result;
	}

	public static final String image64base="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";

	
}
