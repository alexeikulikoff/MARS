package com.mibs.mars.controllers;



import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import com.mibs.mars.config.AppConfig;
import com.mibs.mars.entity.Conclusion;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.RemotePaths;

import com.mibs.mars.entity.Users;
import com.mibs.mars.exception.ErrorCreateProfileException;
import com.mibs.mars.exception.ErrorDicomParsingException;
import com.mibs.mars.exception.FolderNotFoundException;
import com.mibs.mars.exceptions.CreateXMLException;
import com.mibs.mars.repository.ConclusionRepository;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ImagesRepository;
import com.mibs.mars.repository.RemotePathsRepository;

import com.mibs.mars.repository.UsersRepository;
import com.mibs.mars.service.ExplorationUniqueName;
import com.mibs.mars.utils.AppConfigDAO;
import com.mibs.mars.utils.Dcm2Img;
import com.mibs.mars.utils.MUtils;
import com.mibs.mars.xml.XMLRequest;
import jcifs.smb.SmbFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import static  com.mibs.mars.utils.MUtils.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class ConfigController extends AbstractController {

	static Logger logger = LoggerFactory.getLogger(ConfigController.class);
	@Autowired
	private AppConfig appConfig;

	@Autowired 
	private UsersRepository usersRepository;
	
	private String path;
	private String uid;
	
	private ExplorationUniqueName explorationUniqueName;
	public ConfigController() {	}
	public static void printDocument(Document doc) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);
	}
	@RequestMapping(value = { "/getAppConfig" },method = {RequestMethod.GET})
	public @ResponseBody AppConfigDAO getAppConfig(Model model ){
			return new AppConfigDAO(appConfig.getContextPath());
	}
	
}
