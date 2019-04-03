package com.mibs.mars.net;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MailAgent {

	static Logger logger = LoggerFactory.getLogger(MailAgent.class);

	public static void sendMail( String from, String to, String host, String subject, String text, String template) throws MessagingException{
		 Properties properties = System.getProperties();
		 properties.setProperty("mail.smtp.host", host);
		 Session session = Session.getDefaultInstance(properties);
		 try {
	          MimeMessage message = new MimeMessage(session);
	          message.setFrom(new InternetAddress(from));
	          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	          message.setSubject(subject);
	          message.setText(text);
	          message.setContent(template, "text/html; charset=utf-8");
	          Transport.send(message);
	          logger.info("Message send to " + to);
	       } catch (MessagingException msg) {
	    	  throw new MessagingException("Error sending email message to " + to);
	       }
	}
}
