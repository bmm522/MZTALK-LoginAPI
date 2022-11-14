package com.loginAPI.oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoRedirectService {

	public ResponseEntity<String> moveKakaoLoginRedirect() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");
		
		HttpEntity<MultiValueMap<String,String>> request = 
				new HttpEntity<>(headers);
		
		return new RestTemplate().exchange(
				"https://kauth.kakao.com/oauth/authorize?client_id=ef30b1afb54d83d8f24e7a0496ae9498&redirect_uri=http://localhost:8000/login-api/auth/kakao/callback&response_type=code",
				HttpMethod.GET,
				request,
				String.class
				);
		
	}

}
