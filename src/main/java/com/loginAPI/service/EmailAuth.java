package com.loginAPI.service;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailAuth {
	
	public String makeRandomNumber() {
		String randomNumber = "";
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		for(int i = 0 ; i < 6 ; i ++) {
			randomNumber += random.nextInt(10);
		}
		return randomNumber;
	}
	
	public static boolean isValidEmail(String email) {
		String format = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(email);
		if(matcher.matches()) {
			return true;
		}
		log.error("It's not an appropriate email format");
		return false;
	}
	
	public String getContents(String authCode) {
		 StringBuffer contents = new StringBuffer();
		   contents.append("<h1>이메일 인증코드</h1><br><br>");
		   contents.append("<p> 이메일 인증코드는 "+authCode+" 입니다.</p><br>");
		return contents.toString();
	}
}
