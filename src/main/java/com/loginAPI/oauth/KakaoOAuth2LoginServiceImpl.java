package com.loginAPI.oauth;



import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loginAPI.jwt.JwtTokenFactory;
import com.loginAPI.model.KakaoOAuthToken;
import com.loginAPI.model.User;
import com.loginAPI.properties.JwtProperties;
import com.loginAPI.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuth2LoginServiceImpl implements KakaoOAuth2LoginService{

	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private final UserRepository userRepository;
	
	@Override
	public  Map<String,String> getKakaoUserInfo(String accessToken) {
	
		ResponseEntity<String> response = getResponseForUserInfo(accessToken);
		
		return kakaoLogin(getKakaoUserInfoMap(response));
		
	}
	
	private ResponseEntity<String> getResponseForUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+accessToken);
		headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
		
		return new RestTemplate().exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST, 
				new HttpEntity<>(headers),
				String.class
				);
	}		

	private HashMap<String, String> getKakaoUserInfoMap(ResponseEntity<String> response) {
		HashMap<String, String> kakaoUserInfoMap = new HashMap<String, String>();
		
		JsonParser jsonParser = new JsonParser();
		JsonObject kakaoUserInfoJsonObject = (JsonObject) jsonParser.parse(response.getBody());
		
		kakaoUserInfoMap.put("id",kakaoUserInfoJsonObject.get("id").getAsString());
		kakaoUserInfoMap.put("email", kakaoUserInfoJsonObject.get("kakao_account").getAsJsonObject().get("email").getAsString());

		return kakaoUserInfoMap;
	}

	private Map<String, String> kakaoLogin(HashMap<String, String> kakaoUserInfoMap) {
		String provider = "KAKAO";
		String providerId = kakaoUserInfoMap.get("id");
		
		String kakaoUsername = provider+"_"+providerId;
		String kakaoPassword = UUID.randomUUID().toString();
		User user = userRepository.findByUsername(kakaoUsername);
		
		if(user==null) {
			user = User.builder()
					.username(kakaoUsername)
					.password(bCryptPasswordEncoder.encode(kakaoPassword))
					.email(kakaoUserInfoMap.get("email"))
					.provider(provider)
					.providerId(providerId)
					.build();
			
			userRepository.save(user);
			
		}
		
		return new JwtTokenFactory().getJwtToken(user);
		
	}



	
}
