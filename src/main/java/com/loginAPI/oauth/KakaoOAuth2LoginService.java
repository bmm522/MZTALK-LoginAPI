package com.loginAPI.oauth;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface KakaoOAuth2LoginService {

	 Map<String,String> getKakaoUserInfo(String code);


}
