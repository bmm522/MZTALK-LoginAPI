package com.loginAPI.oauth;

import java.util.Map;

public interface KakaoOAuth2LoginService {

	 Map<String,String> getKakaoUserInfo(String code);

}
