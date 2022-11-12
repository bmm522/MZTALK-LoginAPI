package com.loginAPI.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import com.loginAPI.properties.PhoneProperties;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

@Service
@Slf4j
public class PhoneAuthServiceImpl implements PhoneAuthService{
	
	@Override
	public Map<String, Object> phoneAuth(String phone) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String authCode = makeRandomNumber();
		if(isValidPhone(phone)) { //폰번호 유효성 체크
			sendMessage(phone, authCode);	
			map.put("phoneAuthCode", authCode);
			return map;
		}
		
		map.put("phoneAuthCode","Fail sendPhoneAuth");
		return map;
	}

	
	public String makeRandomNumber() {
		String randomNumber = "";
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		for(int i = 0 ; i < 6 ; i ++) {
			randomNumber += random.nextInt(10);
		}
		return randomNumber;
	}
	
	private boolean isValidPhone(String phone) {
		String format = "\\d{3}-\\d{4}-\\d{4}";
		Pattern pattern = Pattern.compile(format);
		Matcher matcher = pattern.matcher(phone);
		if(matcher.matches()) {
			return true;
		}
		log.error("It's not an appropriate phone format");
		return false;
	}


	private void sendMessage(String phone, String authCode) {
		Message coolsms = new Message(PhoneProperties.apiKey, PhoneProperties.apiSecretKey);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("to", phone);
		params.put("from", PhoneProperties.fromNumber);
		params.put("type", "SMS");
		params.put("text", "[grabMe] 인증번호 "+authCode+" 를 입력하세요.");
		params.put("LoginAndSingUpForm", "test app 1.2"); // application name and version

		try {
			JSONObject obj = (JSONObject) coolsms.send(params);
		} catch (CoolsmsException e) {
			log.error("Fail sendMessage");
			System.out.println(e.getMessage());
			System.out.println(e.getCode());
		}
		
	}
}
