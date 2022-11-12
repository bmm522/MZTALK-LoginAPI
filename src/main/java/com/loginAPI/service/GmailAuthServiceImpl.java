package com.loginAPI.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.stereotype.Service;

import com.loginAPI.properties.GmailProperties;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GmailAuthServiceImpl extends EmailAuth implements EmailAuthService{

	
	public Map<String, Object> getEmailAuth(String email) {
		HashMap<String, Object> map = new HashMap<String,Object>();
		String authCode = makeRandomNumber();
		if(isValidEmail(email)) { //이메일 유효성 체크
			sendMail("loginAPI의 이메일인증코드", authCode,email,GmailProperties.gmailId,GmailProperties.gmailPassword);
			map.put("emailAuthCode", authCode);
			return map;
		} 
		map.put("emailAuthCode", "It's not an appropriate email format");
		return map;
	}
	
	public void sendMail(String subject, String authCode, String toMailAddress, String gmailId, String gmailPassword) {
		String fromMail = gmailId;
	    String fromName = "CertificationCode";
	    String toMail = toMailAddress; // 콤마(,) 나열 가능
	    
	    String contents = getContents(authCode);
	    // mail properties
	    Properties props = getProperties();
	   
	    Session mailSession = Session.getInstance(props,
	            new javax.mail.Authenticator() { // set authenticator
	                protected PasswordAuthentication getPasswordAuthentication() {
	                	return new PasswordAuthentication(gmailId, gmailPassword);
	                }
	            });

	    try {
	        MimeMessage message = new MimeMessage(mailSession);

	        message.setFrom(new InternetAddress(fromMail, MimeUtility.encodeText(fromName, "UTF-8", "B"))); // 한글의 경우 encoding 필요
	        message.setRecipients(
	            Message.RecipientType.TO, 
	            InternetAddress.parse(toMail)
	        );
	        message.setSubject(subject);
	        message.setContent(contents, "text/html;charset=UTF-8"); // 내용 설정 (HTML 형식)
	        message.setSentDate(new java.util.Date());
	        
	        System.out.println(1);
	        Transport t = mailSession.getTransport("smtp");
	        t.connect(gmailId, gmailPassword);
	        t.sendMessage(message, message.getAllRecipients());
	        t.close();
	        System.out.println(2);
	    } catch (Exception e) {
	    	log.error("Error sendMail");
	    }
	}

	private Properties getProperties() {
		 Properties props = new Properties();
		props.put("mail.smtp.host", GmailProperties.host); // use Gmail
		props.put("mail.smtp.port", GmailProperties.port); // set port
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // use TLS
		return props;
	}
	
}
