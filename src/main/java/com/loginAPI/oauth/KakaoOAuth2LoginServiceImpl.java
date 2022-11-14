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
import com.loginAPI.service.RegisterServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuth2LoginServiceImpl implements KakaoOAuth2LoginService{

	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private final UserRepository userRepository;
	
	@Override
	public ResponseEntity<?> getKakaoUserInfo(String code) {

		KakaoOAuthToken kakaoOAuthToken= null;
		try {
			kakaoOAuthToken = getAccessToken(kakaoOAuthToken,code);
		} catch (JsonMappingException e) {
			log.error("JSON  and Entity don`t  match ");
		} catch(JsonProcessingException e) {
			log.error("JSON  Proccessiong error ");
		}
		return KakaoLogin( getKakaoUserInfoFromResourceServer(kakaoOAuthToken));
	}
	


	private KakaoOAuthToken getAccessToken(KakaoOAuthToken kakaoOAuthToken, String code) throws JsonMappingException, JsonProcessingException {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

		ResponseEntity<String> response = getResponseForAccessToken(new HttpEntity<>(getBodyForAccessToken(code), headers));
			
		return new ObjectMapper().readValue(response.getBody(),KakaoOAuthToken.class);
	}

	private ResponseEntity<String> getResponseForAccessToken(HttpEntity kakaoAccessTokenRequest) {
		return new RestTemplate().exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST, 
				kakaoAccessTokenRequest,
				String.class
				);
	}

	private MultiValueMap<String, String> getBodyForAccessToken(String code) {
		MultiValueMap<String, String> body  = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", "ef30b1afb54d83d8f24e7a0496ae9498");
		body.add("redirect_uri", "http://localhost:8000/login-api/auth/kakao/callback");
		body.add("code", code);
		return body;
	}
	
	private HashMap<String, String> getKakaoUserInfoFromResourceServer(KakaoOAuthToken kakaoOAuthToken) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+kakaoOAuthToken.getAccess_token());
		headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

		ResponseEntity<String> response = getResponseForUserInfo(new HttpEntity<>(headers));
		
		return getKakaoUserInfoMap(response);
		

	}
	
	private ResponseEntity<String> getResponseForUserInfo(HttpEntity kakaoUserInfoRequest) {
		return new RestTemplate().exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST, 
				kakaoUserInfoRequest,
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

	private ResponseEntity<?> KakaoLogin(HashMap<String, String> kakaoUserInfoMap) {
		String provider = "KAKAO";
		String providerId = kakaoUserInfoMap.get("id");
		
		String kakaoUsername = provider+"_"+providerId;
		String kakaoPassword = UUID.randomUUID().toString();
		User user = userRepository.findByUsername(kakaoUsername);
		
		if(user==null) {
			user = User.builder()
					.username(kakaoUsername)
					.password(kakaoPassword)
					.email(kakaoUserInfoMap.get("email"))
					.provider(provider)
					.providerId(providerId)
					.build();
			
			new RegisterServiceImpl(bCryptPasswordEncoder).register(user);
		}
		
		Map<String, String> jwtToken = new JwtTokenFactory().getJwtToken(user);
		return postToFront(jwtToken);
	}



	private ResponseEntity<?> postToFront(Map<String, String> jwtToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken.get("jwtToken"));
		headers.add("RefreshToken", "RefreshToken "+jwtToken.get("refreshToken"));
		headers.setLocation(URI.create("http://127.0.0.1:5501/study_id_check.html"));
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}

	
}
