package com.loginAPI.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.loginAPI.model.User;
import com.loginAPI.oauth.provider.FacebookUserInfo;
import com.loginAPI.oauth.provider.GoogleUserInfo;
import com.loginAPI.oauth.provider.NaverUserInfo;
import com.loginAPI.oauth.provider.SocialProviderUserInfo;
import com.loginAPI.repository.UserRepository;

@Service
public class PrincipalSocialOAuth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(userRequest);
		
		SocialProviderUserInfo socialProviderUserInfo = null;
		
		switch(userRequest.getClientRegistration().getRegistrationId()) {
		case "google": socialProviderUserInfo = new GoogleUserInfo(oauth2User.getAttributes()); break;
		case "facebook": socialProviderUserInfo = new FacebookUserInfo(oauth2User.getAttributes()); break;
		case "naver": socialProviderUserInfo = new NaverUserInfo(oauth2User.getAttributes()); break;
		}
		
		User user = getUser(socialProviderUserInfo);
	
		
		return super.loadUser(userRequest);
	}

	private User getUser(SocialProviderUserInfo socialProviderUserInfo) {
		
		return null;
	}
}
