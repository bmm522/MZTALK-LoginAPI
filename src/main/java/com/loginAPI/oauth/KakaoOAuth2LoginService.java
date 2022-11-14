package com.loginAPI.oauth;

import org.springframework.http.ResponseEntity;

public interface KakaoOAuth2LoginService {

	ResponseEntity<?> getKakaoUserInfo(String code);

}
