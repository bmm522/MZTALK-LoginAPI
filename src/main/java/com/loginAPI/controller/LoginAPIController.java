package com.loginAPI.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loginAPI.model.User;
import com.loginAPI.oauth.KakaoOAuth2LoginService;
import com.loginAPI.service.EmailAuthService;
import com.loginAPI.service.KakaoLoginService;
import com.loginAPI.service.PhoneAuthService;
import com.loginAPI.service.RegisterService;
import com.loginAPI.service.UserNameDuplicateCheckService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/login-api")
@RestController
@Slf4j
public class LoginAPIController {
	
	@Autowired
	private KakaoOAuth2LoginService kakaoOAuth2LoginService;
	@Autowired
	private RegisterService registerService;
	@Autowired
	private EmailAuthService emailAuthService;
	@Autowired
	private PhoneAuthService phoneAuthService;
	@Autowired
	private UserNameDuplicateCheckService userNameDuplicateCheckService;

	@PostMapping("/register")
	public void register(User user) {
		registerService.register(user);
	}
	
	@ResponseBody
	@PostMapping("/inspection")
	public Map<String, Object> duplicateCheck(@RequestBody String username){
		return userNameDuplicateCheckService.duplicateCheck(asString(username,"username"));
	}
	
	@ResponseBody
	@PostMapping("/email_auth")
	public Map<String, Object> emailAuth(@RequestBody String email) {		
		return  emailAuthService.getEmailAuth(asString(email,"email"));

	}
	
	@ResponseBody
	@PostMapping("/phone_auth")
	public Map<String, Object> phoneAuth(@RequestBody String phone){
		return phoneAuthService.phoneAuth(asString(phone,"phone"));

	}
	
	@ResponseBody
	@GetMapping("/auth/kakao/callback")
	public ReponseEntity<?> kakaoLogin(@Param(value = "code")String code){
		return kakaoOAuth2LoginService.getKakaoUserInfo(code);
		
	}


	
	private String asString(String data,String dataname) {
		try{
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(data);
			return element.getAsJsonObject().get(dataname).getAsString();
		} catch(Exception e) {
			log.error("not JsonObject");
		}
		return "not JsonObject";
	}
	
}
