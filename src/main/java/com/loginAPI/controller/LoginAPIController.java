package com.loginAPI.controller;

import java.util.Map;

import com.loginAPI.oauth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loginAPI.model.User;
import com.loginAPI.service.EmailAuthService;
import com.loginAPI.service.PhoneAuthService;
import com.loginAPI.service.RegisterService;
import com.loginAPI.service.UserNameDuplicateCheckService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.GET;

@RequestMapping("/login-api")
@RestController
@Slf4j
public class LoginAPIController {
	

	@Autowired
	private KakaoOAuth2LegacyLoginServiceImpl kakaoOAuth2LegacyLoginService;
	@Autowired
	private NaverOAuth2LoginService naverOAuth2LoginService;


	@Autowired
	private GoogleOAuth2LoginService googleOAuth2LoginService;

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
		user.setProvider("LOCAL");
		user.setProviderId("NULL");
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
	@PostMapping("/oauth/kakao")
	public Map<String,String> oauthKakaoLogin(@RequestBody String accessToken){
		return kakaoOAuth2LoginService.getKakaoUserInfo(asString(accessToken,"accessToken"));
	}

//	@ResponseBody
//	@PostMapping("/oauth/google")
//	public void oauth2GoogleLogin(@RequestBody String credential){
//		googleOAuth2LoginService.getGoogleUserInfo(asString(credential,"credential"));
//	}
	@ResponseBody
	@PostMapping("/oauth/naver")
	public void oauthNaverLogin(@RequestBody String accessToken){
		System.out.println(accessToken);
		naverOAuth2LoginService.getNaverUserInfo(asString(accessToken, "accessToken"));
	}

	@GetMapping("/auth/kakao/callback")
	public ResponseEntity<?> test(String code){
		System.out.println("실행");
		return kakaoOAuth2LegacyLoginService.getKakaoUserInfo(code);
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
