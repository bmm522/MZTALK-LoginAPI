package com.loginAPI.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.loginAPI.properties.NavermailProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavermailAuthServiceImpl  extends EmailAuth implements EmailAuthService{
	
	public Map<String, Object> getEmailAuth(String email) {
		HashMap<String, Object> map =new HashMap<String, Object>();
		String authCode = makeRandomNumber();
		String contents = getContents(authCode);
		if(isValidEmail(email)) {
			sendMail("이메일 인증코드 입니다.",contents, email);
			map.put("emailAuthCode", authCode);
			return map;
		}
		map.put("emailAuthCode",  "It's not an appropriate email format");
		return map;
	}
	
	public void sendMail(String subejct, String body, Object obj) {
		try {
			InternetAddress[] receiverList = new InternetAddress[1];
			receiverList[0] = new InternetAddress((String)obj);
			
			// SMTP 발송 Properties 설정
			Properties props = getProperties();
			
			// SMTP Session 생성
			Session mailSession = Session.getInstance(props, new javax.mail.Authenticator(){
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(NavermailProperties.MAIL_ID, NavermailProperties.MAIL_PW);
				}
			});
			
			// Mail 조립
			Message mimeMessage = new MimeMessage(mailSession);
			mimeMessage.setFrom(new InternetAddress(NavermailProperties.MAIL_ID));
			mimeMessage.setRecipients(Message.RecipientType.TO, receiverList);
            // 메일 제목
			mimeMessage.setSubject(subejct);
            // 메일 본문 (.setText를 사용하면 단순 텍스트 전달 가능)
			mimeMessage.setContent(body, "text/html; charset=UTF-8");
			
			// Mail 발송
			Transport.send(mimeMessage);
			
		} catch(Exception e) {
			log.error("Error SendMail");
		}
	}

	private Properties getProperties() {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", NavermailProperties.HOST);
		props.put("mail.smtp.port", NavermailProperties.PORT);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.enable","true");
		props.put("mail.smtp.ssl.trust", NavermailProperties.HOST);
		props.put("mail.smtp.auth", "true");
		return props;
	}
}
