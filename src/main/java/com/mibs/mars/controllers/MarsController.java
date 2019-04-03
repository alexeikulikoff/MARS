package com.mibs.mars.controllers;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;  
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mibs.mars.config.AppConfig;
import com.mibs.mars.dao.AddInvitation;
import com.mibs.mars.dao.ConclusionDAO;
import com.mibs.mars.dao.ContactDAO;
import com.mibs.mars.dao.ExplorationDAO;
import com.mibs.mars.dao.ExplorationInvitationDAO;
import com.mibs.mars.dao.InvitationsDAO;
import com.mibs.mars.dao.JournalDAO;
import com.mibs.mars.dao.NetPath;
import com.mibs.mars.dao.PaymentDAO;
import com.mibs.mars.dao.RemotePathWrapper;
import com.mibs.mars.dao.RemotePathsDAO;
import com.mibs.mars.dao.UsersDAO;
import com.mibs.mars.dao.UsersWrapper;
import com.mibs.mars.entity.CheckedEmail;
import com.mibs.mars.entity.Conclusion;
import com.mibs.mars.entity.Contacts;
import com.mibs.mars.entity.Doctors;
import com.mibs.mars.entity.Exploration;
import com.mibs.mars.entity.ExplorationShort;
import com.mibs.mars.entity.ImageEntity;
import com.mibs.mars.entity.Invitations;
import com.mibs.mars.entity.Journal;
import com.mibs.mars.entity.Payments;
import com.mibs.mars.entity.RemotePaths;
import com.mibs.mars.entity.Users;
import com.mibs.mars.exceptions.CabinetBuildException;
import com.mibs.mars.exceptions.CheckedMailException;
import com.mibs.mars.net.MailAgent;
import com.mibs.mars.repository.CheckedEmailRepository;
import com.mibs.mars.repository.ConclusionRepository;
import com.mibs.mars.repository.ContactsRepository;
import com.mibs.mars.repository.DoctorsRepository;
import com.mibs.mars.repository.ExplorationRepository;
import com.mibs.mars.repository.ExplorationShortRepository;
import com.mibs.mars.repository.ImagesRepository;

import com.mibs.mars.repository.UsersRepository;
import com.mibs.mars.service.UsersDetails;
import com.mibs.mars.utils.DicomImage;
import com.mibs.mars.utils.ImageWrapperBuilder;
import com.mibs.mars.utils.ResponseCusomEntity;
import com.mibs.mars.utils.ResponseImage;
import com.mibs.mars.utils.MUtils;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.SessionFactory;

import static com.mibs.mars.utils.MUtils.AYEAR;
import static com.mibs.mars.utils.MUtils.regDate;
import static com.mibs.mars.utils.Messages.*;

@Controller
public class MarsController  extends AbstractController{

	// sudo tcpdump -A -s 0 'tcp port 8080 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0)' -i lo >/home/admin3/ff.dmp
	//  git pull https://github.com/alexeikulikoff/MARS.git m5
	
	
	static Logger logger = LoggerFactory.getLogger(MarsController.class);
	private Locale locale = Locale.getDefault();

	private final String ADMIN_EMAIL = "storage@mcomtech.ru";
	
	@Autowired
	AppConfig appConfig;
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private CheckedEmailRepository checkedEmailRepository;
	@Autowired
	private ExplorationRepository explorationRepository;
	@Autowired
	private ExplorationShortRepository explorationShortRepository;
	
	
	@Autowired
	private ImagesRepository imagesRepository;
	@Autowired
	private ContactsRepository contactsRepository;
	@Autowired
	private DoctorsRepository doctorsRepository;
	@Autowired
	private ConclusionRepository conclusionRepository;
	
	@Autowired
	private SessionFactory sessionFactory;

	public MarsController(){
		logger.info("Start Patient's Private Parlor...");
	}
	private Long T1(){
		return ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toEpochSecond();
	}
	private void saveCheckedEmail(String email, String uuid) throws CheckedMailException{
	
		CheckedEmail entity = checkedEmailRepository.findByEmail(email);
	
		if (entity == null){
			CheckedEmail checked = new CheckedEmail(email, uuid);
			try{
				checkedEmailRepository.save(checked);
			}catch(Exception e){
				
				throw new CheckedMailException(ERROR_SAVE_CHECKED_EMAIL);
			}
		}else{
			try{
				checkedEmailRepository.updateUuid(uuid, T1(), email);
			}catch(Exception e){
				throw new CheckedMailException(ERROR_UPDATE_CHECKED_EMAIL);
			}
		}
	}
	
	@RequestMapping("/")
	public String getRoot(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
			
			Users user = usersRepository.findByEmail( activeUser.getEmail() );
			model.addAttribute("userID", user.getId() );
			if (activeUser.getRoles().equals("ADMIN")) {
				model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
				model.addAttribute("patients",messageSource.getMessage("label.patients", null, locale));
				model.addAttribute("tableId","tbPatients");
				Init(model,activeUser );
				return "redirect:/profile";
			}	
			else if (activeUser.getRoles().equals("PATIENT")) {
				Init(model,activeUser );
				model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
			
				return "redirect:/profile";
			}	
			else if (activeUser.getRoles().equals("DOCTOR")) {
					Init(model,activeUser );
					model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
				
					return "redirect:/profile";
					
			}else {
				return "404";
			}
	}
	@RequestMapping(value = { "/updateNetPath" },method = {RequestMethod.POST})
	public @ResponseBody String  updateNetPath( @RequestBody NetPath  np ) {
		try {
			explorationRepository.updatePath(np.getPath(), np.id);
			return "SUCCESS_PATH_UPDATE";
		}catch(Exception e) {
			//e.printStackTrace();
			return "ERROR_PATH_UPDATE";
		}
	}
	private void deleteDir(Path path) {
		try {
			Files.walk(path)
			  .sorted(Comparator.reverseOrder())
			  .map(Path::toFile)
			  .forEach(File::delete);
		} catch (IOException e) {
		
			logger.error("Error while deleting directory: " + path.getFileName());
		}
	}
	@RequestMapping(value = { "/dropExploration" },method = {RequestMethod.GET})
	public @ResponseBody String  dropExploration( @RequestParam(value="id", required = true)  Long id   ) {
		Exploration exploration = explorationRepository.findById(id);
		if (exploration == null) return "ERROR_DROP_EXPLORATION";
		try {
			 deleteDir( Paths.get( appConfig.getStoragePath() + "/" + exploration.getDicomname() ));
			 deleteDir( Paths.get( appConfig.getSerializedPath() + "/" + exploration.getDicomname()));
			 explorationRepository.delete(exploration);
			return "DROP_EXPLORATION";
		}catch(Exception e) {
			return "ERROR_DROP_EXPLORATION";
		}
	}
	@RequestMapping(value = { "/editNetPath" },method = {RequestMethod.GET})
	public @ResponseBody String  editNetPath( @RequestParam(value="id", required = true)  Long id  ) {

		Exploration exploration = explorationRepository.findById(id);
		if (exploration == null) return "PATH_NOT_FOUND";
		return exploration.getRemotepath();
	}
	@RequestMapping(value = { "/buildCabinet" },method = {RequestMethod.GET})
	public @ResponseBody String  buildCabinet( @AuthenticationPrincipal UsersDetails activeUser  ) {
		Users user = usersRepository.findByEmail( activeUser.getEmail() );
		if (user == null) return "ERROR_USER_NOT_FOUND";
		
		List<Exploration> explorations = explorationRepository.findByUsersId( user.getId());
		if (explorations != null && explorations.size() > 0) {
			for(Exploration expl : explorations) {
				if (expl.getDicomSize() == 0) {
					try {
						buildCabinet( expl, null, null );
					} catch (CabinetBuildException e) {
						 logger.error("Error building Cabinet with message: " + e.getMessage() );
						
						 String[] params = { user.getSurname() + "  " + user.getFirstname() + " " + user.getLastname(), user.getEmail()};
						 String template = messageSource.getMessage("mail.template.newCabinetError", params, locale);
						 String subject =  messageSource.getMessage("mail.template.subject", null, locale);
						 try {
							MailAgent.sendMail(appConfig.getMailFrom(), ADMIN_EMAIL, appConfig.getMaiSmtpHost(),  subject, "", template);
						} catch (MessagingException e1) {
							 logger.error("Error sending email to " + ADMIN_EMAIL );
						}
						return "ERROR_CABINET_BUILDING";
					}
				}
			}
		}
		return "CABINET_BUILDED";
		
	}
	@RequestMapping(value = { "/loadExploration" },method = {RequestMethod.GET})
	public @ResponseBody String  loadExploration( @RequestParam(value="id", required = true)  Long id  ) {
	
		
		Exploration explorations = explorationRepository.findById( id );
		
		if (explorations.getDicomSize() == 0) {
			try {
					buildCabinet( explorations, null, null );
				} catch (CabinetBuildException e) {
					logger.error("Error: " + e.getMessage());
					//e.printStackTrace();
					return "ERROR_CABINET_BUILDING";
			}
		}
		return "CABINET_BUILDED";
		
	}
	@RequestMapping(value = { "/rebuild" },method = {RequestMethod.GET})
	public @ResponseBody String  rebuild( @RequestParam(value="id", required = true)  Long id  ) {
	
		
		Exploration explorations = explorationRepository.findById( id );
		
		if (explorations == null) return "ERROR_CABINET_REBUILD";
		
		Path path = Paths.get( appConfig.getStoragePath() + "/" + explorations.getDicomname() );
		Path serPath = Paths.get( appConfig.getSerializedPath() + "/" + explorations.getDicomname() );
		if (Files.exists(path)) {
			try {
				Files.walk(path)
				  .sorted(Comparator.reverseOrder())
				  .map(Path::toFile)
				  .forEach(File::delete);
				
			} catch (IOException e) {
				return "ERROR_CABINET_REBUILD";
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
				return "ERROR_CABINET_REBUILD";
			}	
		}
		
		return "CABINET_REBUILDED";
		
	}
	@RequestMapping(value = { "/showJournalTable" },method = {RequestMethod.GET})
	public @ResponseBody List<JournalDAO> getJournalTable(@RequestParam(value="d1", required = true)  String d1, @RequestParam(value="d2", required = true)  String d2  ) {
		
		List<JournalDAO>  result = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long e1 =  LocalDate.parse(d1, formatter).atStartOfDay(zoneId).toEpochSecond();
		long e2 =  LocalDate.parse(d2, formatter).atStartOfDay(zoneId).toEpochSecond();
		
		try {
			List<Journal> journals =  journalRepository.findByDateBetween( e1, e2);
			result = new ArrayList<>();
			for(Journal jn : journals) {
				if (jn.getUsers() != null) {
					UsersDAO usr = new UsersDAO();
					usr.setId(jn.getUsers().getId());
					usr.setEmail(jn.getUsers().getEmail());
					usr.setFirstname( jn.getUsers().getFirstname() );
					usr.setLastname( jn.getUsers().getLastname());
					usr.setSurname( jn.getUsers().getSurname());
				
					JournalDAO journal = new JournalDAO();
					journal.setUsers( usr );
					journal.setId(jn.getId());
					journal.setExplname(jn.getExplname());
					journal.setRemotepath(jn.getRemotepath());
					journal.setUniqueid(jn.getUniqueid());
					journal.setDate(jn.getDate());
					journal.setDicomSize(jn.getDicomSize());
					journal.setDicomname(jn.getDicomname());
					journal.setUsersId( usr.getId() );
					result.add( journal );	
				}
			
			}
		}catch(Exception e) {
			logger.error("Error extractions journal table! " + e.getMessage());
		}
		return result;
		
	}
	
	@RequestMapping(value = { "/sendMailReset" },method = {RequestMethod.GET})
	public @ResponseBody String sendMailReset(@RequestParam(value="email", required = true)  String email ) {
		Users user = usersRepository.findByEmail(email);
		if (user == null) return "User with email: " + email + " not found!";
		  try{
			   usersRepository.updateMailed(new Long(0), user.getId());
		   } catch(Exception e) {
			   logger.error("Error while updating mailed time for user  :" + user.getEmail());
			   return "ERROR_UPDATING_MAILED_TIME";
		   }
		  return "User with email: " + email + "  found and updated!";
	}
	
	@RequestMapping(value = { "/sendMailBidding" },method = {RequestMethod.GET})
	public @ResponseBody String sendMailBidding(@RequestParam(value="id", required = true)  Long id ) {
		
		Users user = usersRepository.findById(id);
		try {
			  
			   String[] params = { user.getSurname() + "  " + user.getFirstname() + " " + user.getLastname(), user.getEmail(), user.getPasswd() };
			   String template_newCabinet = messageSource.getMessage("mail.template.newCabinet", params, locale);
			   String subject =  messageSource.getMessage("mail.template.subject", null, locale);
			   MailAgent.sendMail(appConfig.getMailFrom(), user.getEmail(), appConfig.getMaiSmtpHost(),  subject, "", template_newCabinet);
			  // MailAgent.sendMail(appConfig.getMailFrom(), "kulikov@ldc.ru", appConfig.getMaiSmtpHost(),  subject, "", template_newCabinet);
			
			   try{
				   usersRepository.updateMailed(regDate(), id);
			   } catch(Exception e) {
				   logger.error("Error while updating mailed time for user  :" + user.getEmail());
				  
				   return "ERROR_UPDATING_MAILED_TIME";
			   }
			   
			   return regDate() +"";
			   
			 } catch (MessagingException e) {
				
				logger.error("Email to :" + user.getEmail() +" has not been sent!" );
				
				return "ERROR_SENDING_EMAIL";
		}
		
		
		
	}
	@RequestMapping(value = { "/getUser" },method = {RequestMethod.GET})
	public @ResponseBody UsersDAO getUser(@RequestParam(value="id", required = true)  String id ) {
		byte[] image = null;
		UsersDAO rs =new UsersDAO();
		try{
			Long i = Long.parseLong(id);
			Users usr = usersRepository.findById(i);
			rs.setId(usr.getId());
			rs.setFirstname(usr.getFirstname());
			rs.setLastname(usr.getLastname());
			rs.setSurname(usr.getSurname());
			rs.setLogin(usr.getLogin());
			rs.setPasswd(usr.getPasswd());
			rs.setEmail(usr.getEmail());
			rs.setRole( usr.getRoles() );
			rs.setMailed(usr.getMailed());
			image = usr.getPhoto();
			String imageStr = ((image != null) && (image.length == 0)) ? null : Base64.encodeBase64String(image);
			rs.setPhoto( imageStr );
			List<Payments> payments = paymentsRepository.findByUserid(usr.getId());
		
			if (payments != null) {
				payments.forEach(pm->{
					rs.addPayment(  pm );
				});
			}
			
			return rs;
		}catch(Exception e) {
			return rs;
		}
	}
	@RequestMapping(value = { "/test" })
	public @ResponseBody QueryResult test(Model model) {

		List<Contacts> contacts = contactsRepository.findByChildid(54201L);
		contacts.forEach(s->System.out.println(s.getUsers().getSurname()));
	
/*		
		Session session = sessionFactory.openSession();
		SQLQuery query =  session.createSQLQuery("SELECT login, email FROM users" );
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List data = query.list();
		
		for(Object object : data) {
	            Map row = (Map)object;
	            System.out.println("Login: " + row.get("login")); 
	            System.out.println("Email: " + row.get("email")); 
	    }
		session.close();
  */
		return  new QueryResult("Hello ");
		
	}
	@RequestMapping(value = { "/getUsersByContacts" },method = {RequestMethod.GET})
	public @ResponseBody UsersWrapper getUsersByContacts(@RequestParam(value="id", required = true)  Long id ) {
		List<Contacts> contacts = contactsRepository.findByChildid( id );
		UsersWrapper wrapper = new UsersWrapper();
		for(Contacts co : contacts) {
			UsersDAO usd = new UsersDAO();
			Users u = co.getUsers();
			usd.setId(u.getId());
			usd.setLogin(u.getLogin());
			usd.setPasswd(u.getPasswd());
			usd.setFirstname(u.getFirstname());
			usd.setLastname(u.getLastname());
			usd.setSurname(u.getSurname());
			usd.setEmail(u.getEmail());
			String imageStr = Base64.encodeBase64String( u.getPhoto() );
			usd.setPhoto( imageStr );
			wrapper.addData(usd);
		}
		return wrapper;
	}
	
	@RequestMapping(value = { "/getUsers" },method = {RequestMethod.GET})
	public  @ResponseBody UsersWrapper  getUsers(@RequestParam(value="role", required = true)  String role ) {
		byte[] image = null;
		List<Users> users = usersRepository.findByRoles(role);
		UsersWrapper wrapper = new UsersWrapper();
		for(Users u : users) {
			UsersDAO usd = new UsersDAO();
			usd.setId(u.getId());
			usd.setLogin(u.getLogin());
			usd.setPasswd(u.getPasswd());
			usd.setFirstname(u.getFirstname());
			usd.setLastname(u.getLastname());
			usd.setSurname(u.getSurname());
			usd.setEmail(u.getEmail());
			
			image = u.getPhoto();
			String imageStr = Base64.encodeBase64String(image);
			usd.setPhoto( imageStr );
			wrapper.addData(usd);
		}
		return wrapper;
	}
	
	@RequestMapping("/explorations")
	public String showExplorations(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		Users user = usersRepository.findByEmail( activeUser.getEmail() );
		Init(model,activeUser );
		model.addAttribute("userRole",user.getRoles());
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
			model.addAttribute("patients",messageSource.getMessage("label.patients", null, locale));
			model.addAttribute("tableId","tbPatients");
			return "admin/explorations";
		}	
		else if (activeUser.getRoles().equals("PATIENT")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
			model.addAttribute("userID", user.getId() );
			return "patient/explorations";
		}	
		else if (activeUser.getRoles().equals("DOCTOR")) {
			model.addAttribute("tableId","tbPatients");
			model.addAttribute("userID",user.getId());
			model.addAttribute("pageHeader",messageSource.getMessage("label.explorations", null, locale));
			return "doctor/explorations";
		}else {
			return "404";
		}
	}
	
	
	@RequestMapping("/showDicom")
	public @ResponseBody DicomImage showDicom( @AuthenticationPrincipal UsersDetails activeUser, @RequestParam(value="id", required = true)  Long id ) {
	
		ImageWrapperBuilder ImageWrapperBuilder = new ImageWrapperBuilder(imagesRepository, explorationRepository, appConfig);
		DicomImage dicomImage = ImageWrapperBuilder.buildDicomImage(id);
		
		return dicomImage;
	}
	@RequestMapping("/getExploration")
	public @ResponseBody ResponseCusomEntity getExploration( @AuthenticationPrincipal UsersDetails activeUser, @RequestParam(value="id", required = true)  Long id ) {
		try{
			Users user = usersRepository.findById(id);
			String userName = user.getSurname() + " " + user.getFirstname() + " " + user.getLastname();
			List<ExplorationDAO> explDAO = new ArrayList<>();
			List<ExplorationShort> explorations = explorationShortRepository.findByUsersId(id);
			ResponseCusomEntity rs = new ResponseCusomEntity(userName, explorations.size() );
			for(ExplorationShort e : explorations) {
				ExplorationDAO dao = new ExplorationDAO( e  );
				List<Conclusion> concls = conclusionRepository.findByExplorationid(e.getId());
				for(Conclusion co : concls) {
					dao.addConclusions( new ConclusionDAO(co.getId(),co.getExplorationid(), co.getFilename()));
				}
				explDAO.add( dao );
			}
			rs.setExplorations( explDAO );
			return rs;
		}catch(Exception e) {
			return  null;
		}
	}

	@RequestMapping("/patients")
	public String showPatients(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		Init(model,activeUser );
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.patients", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.patient", null, locale));
			model.addAttribute("usersTitle",messageSource.getMessage("label.patients", null, locale));
			model.addAttribute("tableId","tbPatients");
			return "admin/users";
			
		}else {
			return "404";
		}
	}
	@RequestMapping("/contacts")
	public String showContacts(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.contacts", null, locale));
			Init(model,activeUser );
			return "admin/contacts";
		}
		else if (activeUser.getRoles().equals("PATIENT")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.contacts", null, locale));
			Init(model,activeUser );
			return "patient/contacts";
		
		}else {
			return "404";
		}
	}
	
	@RequestMapping("/invitations")
	public String showInvitations(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		
		Users user = usersRepository.findByEmail( activeUser.getEmail() );
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.invitations", null, locale));
			Init(model,activeUser );
			return "admin/invitations";
		}
		else if (activeUser.getRoles().equals("PATIENT")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.invitations", null, locale));
			Init(model,activeUser );
			model.addAttribute("userID", user.getId() );
			return "patient/invitations";
		
		}else {
			return "404";
		}
	}
	
	@RequestMapping("/help")
	public String help(Model model, @AuthenticationPrincipal UsersDetails activeUser ) {
		
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.help", null, locale));
			Init(model,activeUser );
			return "admin/help";
		}
		else if (activeUser.getRoles().equals("PATIENT")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.help", null, locale));
			Init(model,activeUser );
			return "patient/help";
			
		}else {
			return "404";
		}	
	}
	@RequestMapping("/dashboard")
	public String dashboard(Model model, @AuthenticationPrincipal UsersDetails activeUser ) {
		Users users = usersRepository.findByEmail(activeUser.getEmail());
		if (users == null) return "404";
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.dashboard", null, locale));
		//	model.addAttribute("profileType",messageSource.getMessage("label.admin", null, locale));
			
			Init(model,activeUser );
			return "admin/dashboard";
		}
		return "404";
		
	}
	@RequestMapping("/profile")
	public String profile(Model model, @AuthenticationPrincipal UsersDetails activeUser ) {
		Users users = usersRepository.findByEmail(activeUser.getEmail());
		if (users == null) return "404";
		model.addAttribute("ProfileuserID",users.getId());
		model.addAttribute("userRole",users.getRoles());
		model.addAttribute("userFullName",users.getSurname() + " " + users.getFirstname() + " " + users.getLastname());
		
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.userProfile", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.admin", null, locale));
			
			Init(model,activeUser );
			return "admin/profile";
		}
		if (activeUser.getRoles().equals("PATIENT")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.userProfile", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.patient", null, locale));
			List<Payments> payments = paymentsRepository.findByUserid( users.getId() );;
			if ((payments != null) && payments.size() > 0) {
				String[] prolongedTime = new String[] {  payments.get(payments.size() - 1).getPaidtill() };
				model.addAttribute("prolongedTime",messageSource.getMessage("label.prolonged", prolongedTime, locale));
			}
			Init(model,activeUser );
	
			return "patient/profile";
		}	
		if (activeUser.getRoles().equals("DOCTOR")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.userProfile", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.doctor", null, locale));
			
			Init(model,activeUser );
			return "doctor/profile";	
		}
		return "404";
	}
	@RequestMapping("/profile2")
	public String profile2(Model model, @AuthenticationPrincipal UsersDetails activeUser ) {
		return "profile2";	
	}
	@RequestMapping("/doctors")
	public String showDoctors(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		Init(model,activeUser );
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.docs", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.doctor", null, locale));
			model.addAttribute("usersTitle",messageSource.getMessage("label.docs", null, locale));
			model.addAttribute("tableId","tbDoctors");
			return "admin/users";
			
		}else {
			return "404";
		}
	}
	
	@RequestMapping("/admins")
	public String showAdmins(Model model,  @AuthenticationPrincipal UsersDetails activeUser ) {
		Init(model,activeUser );
		if (activeUser.getRoles().equals("ADMIN")) {
			model.addAttribute("pageHeader",messageSource.getMessage("label.admins", null, locale));
			model.addAttribute("profileType",messageSource.getMessage("label.admin", null, locale));
			model.addAttribute("usersTitle",messageSource.getMessage("label.admins", null, locale));
			
			model.addAttribute("tableId","tbAdmins");
			return "admin/users";
		}else {
		
			return "404";
		}
	}
	@RequestMapping("/getInvitations")
	public @ResponseBody List<ExplorationInvitationDAO> getInvitations( @AuthenticationPrincipal UsersDetails activeUser, @RequestParam(value="id", required = true)  Long id ) {
		List<ExplorationInvitationDAO> explorationInvitationDAOs = new ArrayList<>();
		Users patient = usersRepository.findById(id);
		List<Exploration>  explorations = explorationRepository.findByUsersId( patient.getId() );
		for(Exploration ex : explorations) {
			ExplorationInvitationDAO explorationInvitationDAO = new ExplorationInvitationDAO( ex );
			List<Invitations> ivitations  = invitationsRepository.findByPatientidAndExplorationid(patient.getId(), ex.getId());
			if (ivitations != null) {
				for(Invitations in : ivitations) {
					Users doctor = usersRepository.findById( in.getDoctorid() );
					InvitationsDAO dao = new InvitationsDAO();
					dao.setId( in.getId() );
					dao.setPatientid(in.getPatientid());
					dao.setExplorationid(in.getExplorationid());
					dao.setDoctorphoto(Base64.encodeBase64String(doctor.getPhoto()) );
					dao.setDoctorname( doctor.getSurname() + ' ' + doctor.getFirstname() + ' ' + doctor.getLastname());
					dao.setDoctorid( doctor.getId() );
					dao.setDate( MUtils.UnixTimeToStringDateOnly ( in.getDate( )) );
					dao.setComments( in.getComments() );
					dao.setExplorationid(ex.getId());
					
					Doctors docs = doctorsRepository.findByUserid(doctor.getId());
					dao.setDoctorwork(docs.getPost());
					explorationInvitationDAO.addInvitations( dao );
				}
			}
			explorationInvitationDAOs.add( explorationInvitationDAO );
		}
		return explorationInvitationDAOs;
	}
	
	@RequestMapping("/addInvitation")
	public @ResponseBody QueryResult addInvitation(@RequestBody AddInvitation invit) {
		Contacts contacts = contactsRepository.findById(invit.getContactid());
		Users doctor = usersRepository.findById(contacts.getChildid());
		Users patient = usersRepository.findById(contacts.getOwnerid());
		try {
			Invitations invitations = new Invitations();
			invitations.setDate( regDate() );
			invitations.setComments(invit.getComments());
			invitations.setPatientid( contacts.getOwnerid() );
			invitations.setDoctorid( contacts.getChildid() );
			invitations.setExplorationid( invit.getExplorationid() );
			Invitations savedInvitation  = invitationsRepository.save(invitations);
			
			String subject =  messageSource.getMessage("mail.template.subject", null, locale);
			String text = "";
			String doctorName = doctor.getFirstname() + " " + doctor.getLastname();
			String patientName = patient.getSurname() + " " + patient.getFirstname() + " " + patient.getLastname();
			
			String[] params = {doctorName, patientName, doctor.getEmail(), doctor.getPasswd()  };
			
			String template = messageSource.getMessage("mail.template.invite", params, locale);
			try {
				MailAgent.sendMail(fromMail,doctor.getEmail(), appConfig.getMaiSmtpHost(),  subject, text, template);
				logger.error("Invitation email  to :" + doctor.getEmail() +" has been sent!" );
				return new QueryResult(SUCCESS_INVITATION_SAVE);
			} catch (MessagingException e) {
			//	e.printStackTrace();
				logger.error("Invitation Email to :" + doctor.getEmail() +" has not been sent!" );
				invitationsRepository.delete(savedInvitation);
				return new QueryResult(ERROR_INVITATION_SAVE);
			}
			
		}catch( DataIntegrityViolationException e1) {
			return new QueryResult(ERROR_INVITATION_SAVE);
		} 
	}
	@RequestMapping("/checkMail")
	public @ResponseBody QueryResult checkMail(@RequestBody ProfileRequest pr) {

		Users user = usersRepository.findByEmail(pr.getEmail());
		if (user == null){
			return  new QueryResult(error01);
		}
		
		String subject =  messageSource.getMessage("mail.template.subject", null, locale);
		String text = user.getSex().equals(sexM) ? messageSource.getMessage("mail.template.textM", null, locale) : messageSource.getMessage("mail.template.textW", null, locale) ;
		text = text + " " +  user.getFirstname() + "  " + user.getLastname()  + "!";
		final String uuid = UUID.randomUUID().toString();
		final String url = "http://" + appConfig.getServerAddress() +":" 
									 + appConfig.getServerPort() + appConfig.getContextPath() 
									 + "/verifyMail?uuid=" + uuid; 
		
		
		String temp1 =  messageSource.getMessage("mail.template.temp1", null, locale);
		String temp2 =  messageSource.getMessage("mail.template.temp2", null, locale);
		String template = temp1 + url + temp2;
		try{
			saveCheckedEmail(pr.getEmail(),uuid);
			try{
				MailAgent.sendMail(fromMail, pr.getEmail(), appConfig.getMaiSmtpHost(),  subject, text, template);
				return new QueryResult(statusOK);
			}catch(MessagingException e){
				return new QueryResult(error02);
			}
		}catch(CheckedMailException c){
			return new QueryResult(error03);
		}
	}
	
	private void showMailConfirmError(Model model){
		String error_confirm_mail =  messageSource.getMessage("error.confirm_mail", null, locale);
		model.addAttribute("msg", error_confirm_mail);
	}
	private void showConfirmEmail(Model model){
		String confirm_mail =  messageSource.getMessage("mail.confirmed", null, locale);
		model.addAttribute("msg", confirm_mail);
	}
	@RequestMapping(value = { "/verifyMail" },method = {RequestMethod.GET})
	public String verifyMail(@RequestParam(value="uuid", required = true) String uuid, Model model  ) {
		CheckedEmail entity = checkedEmailRepository.findByUuid( uuid );
		if (entity == null){
			showMailConfirmError(model);
		}else{
		
			int mailConfirmPeriod = 300;
			try{
				mailConfirmPeriod = Integer.parseInt(appConfig.getMaiConfirmPeriod());
			}catch(NumberFormatException e){
				String error =  messageSource.getMessage("error.parsing_mail_confirm_period", null, locale);
				logger.info( error );
			}
			Long t1 = entity.getT1();
			Long t2 = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toEpochSecond();
			Duration d = Duration.ofSeconds(t2 - t1);
			if (d.getSeconds() <= mailConfirmPeriod){
				Users users = usersRepository.findByEmail( entity.getEmail() );
				if (users == null){
					showMailConfirmError(model);
				}else{
					try{
						usersRepository.updateMailed(1L,users.getId());
						showConfirmEmail(model);
					}catch(Exception e){
						//e.printStackTrace();
						showMailConfirmError(model);
					}
				}
			}
		}
		return "confirm";
	}
	@RequestMapping(value = { "/getEmailStatus" },method = {RequestMethod.GET})
	public @ResponseBody QueryResult getEmailStatus(@RequestParam(value="email", required = true)  String email) {
		
		Users users = usersRepository.findByEmail(email);
		if (users == null){
			return new QueryResult(ERROR_EMAIL_NOT_EXIST);
		}else{
			if (users.getMailed() > 0){
				return new QueryResult(EMAIL_IS_CONFIRMED);
			}else{
				return new QueryResult(EMAIL_IS_NOT_CONFIRMED);
			}
		}
	}
	@RequestMapping(value = { "/getContactByEmail" },method = {RequestMethod.GET})
	public @ResponseBody ContactDAO getContactByEmail(@RequestParam(value="email", required = true)  String email) {
		
		Users user =  usersRepository.findByEmail(email) ;
		if (user == null) return new ContactDAO("NOT_EXIST");
		Doctors doctor = doctorsRepository.findByUserid(user.getId());
		ContactDAO dao = new ContactDAO("EXIST");
		dao.setId(user.getId());
		dao.setFirstname( user.getFirstname() );
		dao.setLastname(user.getLastname());
		dao.setSurname(user.getSurname());
		dao.setEmail(user.getEmail());
		dao.setPhoto(( user.getPhoto()==null ) ? MUtils.empty_image : Base64.encodeBase64String(user.getPhoto()));
		dao.setChildwork(  doctor !=null ? doctor.getWorks() : " ");
		dao.setChildphone( doctor !=null ? doctor.getPhone() : " " );
		dao.setChildpost(  doctor !=null ? doctor.getPost() : " ");
		return dao;
	}
	@RequestMapping(value = { "/changeaccess" },method = {RequestMethod.GET})
	public @ResponseBody QueryResult changeaccess(@RequestParam(value="id", required = true)  Long id ) 
	{
		try {
			Contacts cont = contactsRepository.findById(id);
			contactsRepository.updateAccess(id, 1 - cont.getAccessed());
			return new QueryResult(SUCCESS_CONTACT_UPDATE);
		}catch(Exception e) {
			
			return new QueryResult(ERROR_CONTACT_UPDATE);
		}
	}
	@RequestMapping(value = { "/hideContact" },method = {RequestMethod.GET})
	public @ResponseBody QueryResult hideContact(@RequestParam(value="id", required = true)  Long id ) 
	{
		try {
			contactsRepository.updateHidden(id, new Long(1));
			return new QueryResult(SUCCESS_CONTACT_UPDATE);
		}catch(Exception e) {
			
			return new QueryResult(ERROR_CONTACT_UPDATE);
		}
	}
	@RequestMapping(value = { "/getContacts" },method = {RequestMethod.GET})
	public @ResponseBody List<ContactDAO> getContacts(Model model, @AuthenticationPrincipal UsersDetails activeUser ) 
	{
		Users currentUser = usersRepository.findByEmail(activeUser.getEmail()) ;
		List<Contacts> contacts = contactsRepository.findByOwneridAndHidden(currentUser.getId(), new Long(0));
		if (contacts == null) return null;
		List<ContactDAO> daos = new ArrayList<>();
		contacts.forEach(cnt->{
			Users contactUser = usersRepository.findById(cnt.getChildid());
			
			if (contactUser != null) {
				Doctors doctor = doctorsRepository.findByUserid(cnt.getChildid());
				if (doctor != null) {
				ContactDAO dao = new ContactDAO("EXIST");
				dao.setId(cnt.getId());
				dao.setFirstname( contactUser.getFirstname() );
				dao.setLastname(contactUser.getLastname());
				dao.setSurname(contactUser.getSurname());
				dao.setEmail(contactUser.getEmail());
				dao.setPhoto(Base64.encodeBase64String(contactUser.getPhoto()));
				dao.setChildwork(  doctor !=null ? doctor.getWorks() : " ");
				dao.setChildphone( doctor !=null ? doctor.getPhone() : " " );
				dao.setChildpost(  doctor !=null ? doctor.getPost() : " ");
				dao.setAccessed(cnt.getAccessed());
				dao.setHidden(cnt.getHidden());
				daos.add( dao );
			  }
			}
			
			});
		return daos;
	}
	@RequestMapping(value = { "/createNewContact" },method = {RequestMethod.POST})
	public @ResponseBody QueryResult addContact(@RequestBody ContactDAO contact, @AuthenticationPrincipal UsersDetails activeUser ) 
	{
		try {
			createNewContact(contact,activeUser);
			return new QueryResult(SUCCESS_CONTACT_CREATE);
		}catch(Exception e) {
			e.printStackTrace();
			return new QueryResult(ERROR_CONTACT_CREATE);
		}
	}
	
	private void createNewContact(ContactDAO contact, UsersDetails activeUser ) {
		Users user =  usersRepository.findByEmail(contact.getEmail()) ;
		if (user == null) {
			Users usr = new Users();
			usr.setEmail( contact.getEmail() );
			usr.setFirstname(contact.getFirstname());
			usr.setLastname(contact.getLastname());
			usr.setSurname(contact.getSurname());
			usr.setRoles("DOCTOR");
			usr.setPasswd( UUID.randomUUID().toString().substring(0, 8) );
			usr.setLogin( contact.getEmail() );
			usr.setRegdate( MUtils.regDate() );
		
			if ( !contact.getPhoto().contains("img/mibs-empty-profile.jpg") ) {
			
				usr.setPhoto( javax.xml.bind.DatatypeConverter.parseBase64Binary( contact.getPhoto().split(",")[1]));
			}
			else{
				usr.setPhoto( javax.xml.bind.DatatypeConverter.parseBase64Binary(MUtils.empty_image) );
			}
			usersRepository.save(usr);
			createNewContact( contact, activeUser );
		}else {
			Users patientUser = usersRepository.findByEmail( activeUser.getEmail() );
			Users doctorUser = usersRepository.findByEmail( contact.getEmail() );
			Doctors docs = doctorsRepository.findByUserid(doctorUser.getId());
			if (docs == null) {
				Doctors doctor = new Doctors();
				doctor.setUserid( doctorUser.getId() );
				doctor.setWorks( contact.getChildwork() );
				doctor.setPhone(contact.getChildphone());
				doctor.setPost( contact.getChildpost() );
				doctorsRepository.save(doctor);
			}
			Contacts testCnt = contactsRepository.findByOwneridAndChildid(patientUser.getId(), doctorUser.getId());
			if (testCnt == null) {
				Contacts cnt = new Contacts();
				cnt.setOwnerid(patientUser.getId());
				cnt.setChildid(doctorUser.getId());
				cnt.setAccessed(1L);
				cnt.setHidden(0L);
				contactsRepository.save(cnt);
			}else {
				contactsRepository.updateHidden( testCnt.getId(), new Long(0));
			}
		}
	}
	@RequestMapping("/dropUser")
	public @ResponseBody QueryResult dropUser(@RequestBody UsersDAO usr ) {
		try {
			Users user = usersRepository.findById(usr.getId());
			if (user != null) {
				List<Exploration> expls = explorationRepository.findByUsersId( user.getId() );
				if (expls != null) {
					for(Exploration ex : expls) {
						explorationRepository.delete(ex);
					}
				}
				usersRepository.delete(user);
			}
			return new QueryResult(QUERY_STATUS_FINE);
		}catch(Exception e) {
			return new QueryResult(ERROR_USER_DROP);
		}
	}

	
	@RequestMapping("/usersCreateNew")
	public @ResponseBody QueryResult usersCreateNew(@RequestBody UsersDAO usr ) {
		try {
			Users user = new Users( usr, usr.getRole() );
			usersRepository.save(user);
			return new QueryResult(SUCCESS_USER_SAVE);
		}catch(Exception e) {
		
			if (e.getMessage().contains("usr_email_key")) {
				return new QueryResult(ERROR_DUBLE_EMAIL);
			}if(e.getMessage().contains("usr_login_key")){
				return new QueryResult(ERROR_DUBLE_LOGIN);
			
			}else{
				return new QueryResult(ERROR_USER_SAVE);
			}
			
		}
	}
	@RequestMapping("/uploadTempImage")
	public @ResponseBody ResponseImage uploadTempImage(@RequestParam("file") MultipartFile file){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			 InputStream input  = file.getInputStream();
			 int length;
	         byte[] buffer = new byte[1024];
	         while ((length = input.read(buffer)) != -1){
	        	 out.write(buffer, 0, length);
	         }
	         byte[] image = out.toByteArray();
	       
	         return new ResponseImage( image );
	       
		   } catch (IOException e) {
			  
			   return new ResponseImage( null );
		}
		
	}
	@RequestMapping("/usersUpdateAll")
	public @ResponseBody QueryResult usersUpdateAll(@RequestBody UsersDAO u  ) 
	{
		try {
			 String base64Image = u.getPhoto().equals( "img/mibs-empty-profile.jpg" ) ? MUtils.image64base : u.getPhoto().split(",")[1];
			 byte[] image = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
	         usersRepository.updateAll(u.getPasswd(), u.getFirstname(), u.getLastname(), u.getSurname(), u.getEmail(), image, u.getId());
		   } catch (Exception e) {
			 e.printStackTrace();
			   return new QueryResult(ERROR_USER_UPDATE);
		}
		
		return new QueryResult(SUCCESS_USER_UPDATE);
	}
	@RequestMapping(value = { "/restore" },method = {RequestMethod.GET})
	public String sendPassword(Model model, @RequestParam(value="email", required = true)  String email ) {
		Users user  = usersRepository.findByEmail( email );
		if (user != null) {
			model.addAttribute("restore_message_ok",  messageSource.getMessage("label.passwordSend", new String[]{ email }, locale));
			String subject =  messageSource.getMessage("mail.template.subject", null, locale);
			String text = messageSource.getMessage("mail.template.textM", null, locale) + "  " +  user.getFirstname() + "  " + user.getLastname()  + "!";
			String[] params = { user.getFirstname() + " " +  user.getLastname() , user.getPasswd() };
			String template = messageSource.getMessage("mail.template.forgotPassword", params, locale);
			try {
				MailAgent.sendMail(fromMail, user.getEmail(), appConfig.getMaiSmtpHost(),  subject, "", template);
				logger.error("Password recovery email  to :" + user.getEmail() +" has been sent!" );
			} catch (MessagingException e) {
			//	e.printStackTrace();
				logger.error("Email to :" + user.getEmail() +" has not been sent!" );
			}
			
		}else {
			model.addAttribute("restore_message_fail",  messageSource.getMessage("label.passwordSendfail", new String[]{ email }, locale));
		}
		return "forgot_password";
	}
	
	
/*	@RequestMapping("/updateEmail")
	public @ResponseBody QueryResult updateEmail(@RequestBody EmailRequest request ) {
		String email = request.getEmail();
		Long id;
		if (email == null ){
			return new QueryResult(ERROR_EMAIL_IS_EMPTY);
		}
		try{
			id = Long.parseLong(request.getId());
		}catch(Exception e){
			//return new QueryResult(ERROR_UPDATE_EMAIL);
		}
		Users users = usersRepository.findById(id);
		if (users == null){
			return new QueryResult(ERROR_PROFILE_NOT_FOUND);
		}
		if(!email.equals(users.getEmail())){
			try{
				usersRepository.updateEmail(email, 0, id);
				
				return new QueryResult(QUERY_STATUS_FINE);
			}catch(Exception e){
				//return new QueryResult(ERROR_UPDATE_EMAIL);
			}
		}else{
			return new QueryResult(QUERY_STATUS_FINE);
		}
	}
*/	

	@RequestMapping(value = { "/updateImage" },method = {RequestMethod.GET})
	public  @ResponseBody ResponseImage updateImage( @RequestParam(value="email", required = true)  String email ) {
		
		Users users = usersRepository.findByEmail( email );
		if (users == null){
			return new ResponseImage(null);
		}
		return new ResponseImage( users.getPhoto() );
	}
	@RequestMapping(value = { "/removeImage" },method = {RequestMethod.GET})
	public @ResponseBody QueryResult removeImage( @RequestParam(value="email", required = true)  String email ) {
		
		Users users = usersRepository.findByEmail( email );
		if (users == null){
			return new QueryResult(ERROR_IMAGE_REMOVE);
		}
	    try{
	    	usersRepository.updateImage(null, users.getId());
	    }catch(Exception e){
	    	return new QueryResult(ERROR_IMAGE_REMOVE);
	    }
		return new QueryResult(SUCCESS_IMAGE_REMOVE);
	}
	@RequestMapping("/saveConclusion")
	public @ResponseBody QueryResult saveConclusion(@RequestParam("conclusion") MultipartFile conclusion, @RequestParam("id") Long id  ) {
		try {
			Exploration exploration = explorationRepository.findById(id);
		
			addConclusions(conclusion, exploration);
			return new QueryResult(SUCCESS_CONCLUSION_SAVE);
		}catch(Exception e) {
			return new QueryResult(ERROR_CONCLUSION_SAVE);
		}
	
	}
	@RequestMapping("/saveProfileImage")
	public @ResponseBody QueryResult saveProfileImage(@RequestParam("file") MultipartFile file, @RequestParam("email") String email  ) {

		Users users = usersRepository.findByEmail( email );
		if (users == null) {
			  return new QueryResult(ERROR_IMAGE_SAVE);
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			 InputStream input  = file.getInputStream();
			 int length;
	         byte[] buffer = new byte[1024];
	         while ((length = input.read(buffer)) != -1){
	        	 out.write(buffer, 0, length);
	         }
	         byte[] image = out.toByteArray();
	         usersRepository.updateImage(image, users.getId());
	        
	         input.close();
		   } catch (IOException e) {
			   return new QueryResult(ERROR_IMAGE_SAVE);
		}
		
	   return new QueryResult(SUCCESS_IMAGE_SAVE);
	}

	

	@RequestMapping("/login")
	public String login01(Model model, @AuthenticationPrincipal UsersDetails activeUser) {
		
		return "login";
	}
	@RequestMapping("/forgot_password")
	public String forgot_password(Model model, @AuthenticationPrincipal UsersDetails activeUser) {
		
		return "forgot_password";
	}
	@RequestMapping("/settings")
	public String settings(Model model, @AuthenticationPrincipal UsersDetails activeUser) {
		Init(model, activeUser );
		//List<RemotePaths> remotePaths = remotePathsRepository.findAll();
		return "admin/settings";
	}
	
	@RequestMapping("/index")
	public String index(Model model, @AuthenticationPrincipal UsersDetails activeUser) {
		Init(model, activeUser );
		//List<RemotePaths> remotePaths = remotePathsRepository.findAll();
		return "redirect:/profile";
	}
	
	@RequestMapping("/getSettings")
	public @ResponseBody RemotePathWrapper getSettings(Model model) {
		List<RemotePaths> remotePaths = remotePathsRepository.findAll();
			if (remotePaths != null) {
			int numTotal = remotePaths.size();
			int numConnected = 0;
			for(RemotePaths r : remotePaths ) {
				if (r.getChecked() == 1) numConnected++;
			}
			RemotePathWrapper remotePathWrapper = new RemotePathWrapper(numTotal,numConnected, remotePaths );
			return remotePathWrapper;
		}else {
			return null;
		}
	}
	
	@RequestMapping("/updateRemotePath")
	public @ResponseBody QueryResult updateRemotePath(@RequestBody RemotePathsDAO rp ) {
		try {
			remotePathsRepository.updateAll(rp.getLogin(), rp.getPasswd(), rp.getDepartment(), rp.getIpaddress(), rp.getDirname(), rp.getUniqueid(), rp.getId());
			return new QueryResult(SUCCESS_REMOUTE_PATH_UPDATE);
		}catch(Exception e) {
			return new QueryResult(ERROR_REMOUTE_PATH_UPDATE);
		}
	}
	@RequestMapping("/dropRemotePath")
	public @ResponseBody QueryResult dropRemotePath(@RequestBody RemotePathsDAO rp ) {
		try {
			RemotePaths pr = remotePathsRepository.findById(rp.getId());
			remotePathsRepository.delete(pr);
			return new QueryResult(SUCCESS_REMOUTE_PATH_DELETE);
		}catch(Exception e) {
			return new QueryResult(ERROR_REMOUTE_PATH_DELETE);
		}
	}
	
	@RequestMapping("/addPayment")
	public @ResponseBody UsersDAO addPayment(@RequestBody PaymentDAO rp ) {
		try {
			Float sum = Float.parseFloat(rp.getSum());
			Users user = usersRepository.findByEmail(rp.getEmail());
			Payments payments = new Payments();
			payments.setUserid( user.getId() );
			payments.setPaiddate( regDate() );
			payments.setPaidsum( sum );
			payments.setPaidtill( rp.getPeriod() *  AYEAR + regDate()  );
			payments.setComments( rp.getComments() );
			paymentsRepository.save(payments);

			UsersDAO rs =new UsersDAO();
			rs.setId(user.getId());
			rs.setFirstname(user.getFirstname());
			rs.setLastname(user.getLastname());
			rs.setSurname(user.getSurname());
			rs.setLogin(user.getLogin());
			rs.setPasswd(user.getPasswd());
			rs.setEmail(user.getEmail());
			byte[] image = user.getPhoto();
			String imageStr = ((image != null) && (image.length == 0)) ? null : Base64.encodeBase64String(image);
			rs.setPhoto( imageStr );
			List<Payments> paymentsSaved = paymentsRepository.findByUserid(user.getId());
			
			if (paymentsSaved != null) {
					paymentsSaved.forEach(pm->{
						rs.addPayment(  pm );
					});
			}
			return rs;
			
		}catch(Exception e) {
			
			return new UsersDAO();
		}
	}
	@RequestMapping("/saveRemotePath")
	public @ResponseBody QueryResult saveRemotePath(@RequestBody RemotePathsDAO rp ) {
		try {
			RemotePaths pr = new RemotePaths( rp );
			remotePathsRepository.save(pr);
			return new QueryResult(SUCCESS_REMOUTE_PATH_SAVE);
		}catch(Exception e) {
			return new QueryResult(ERROR_REMOUTE_PATH_SAVE);
		}
	}
	@RequestMapping("/testPathConnection")
	public @ResponseBody QueryResult testPathConnection(Model model, @RequestParam(value="id", required = true)  Long id) {
		RemotePaths rp = remotePathsRepository.findById(id);
		String url = rp.getIpaddress() + "/" + rp.getDirname();
		QueryResult result = MUtils.testRemouteStorage(url, rp.getLogin(), rp.getPasswd()) ? new QueryResult("SUCCESS_TEST_REMOUTE_PATH"): new QueryResult("ERROR_TEST_REMOUTE_PATH");
		if (result.getMessage().equals("SUCCESS_TEST_REMOUTE_PATH")) {
			remotePathsRepository.updateCheked(1, id);
		}else {
			remotePathsRepository.updateCheked(0, id);
		}
		return result;
	}
	
	@RequestMapping("/getSettingsByID")
	public @ResponseBody RemotePaths getSettingsByID(Model model, @RequestParam(value="id", required = true)  Long id) {
		RemotePaths remotePaths = remotePathsRepository.findById(id);
		return remotePaths;
	}
	
	@RequestMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "login";
	}
	private void Init(Model model, UsersDetails activeUser ) {
		Users user = usersRepository.findByEmail( activeUser.getEmail() );
		model.addAttribute("image",Base64.encodeBase64String( user.getPhoto() ));
		model.addAttribute("surname",user.getSurname() );
		model.addAttribute("firstname",user.getFirstname());
		model.addAttribute("lastname",user.getLastname());
	}
}
