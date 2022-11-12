package com.loginAPI.oauth;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.loginAPI.jwt.JwtTokenFactory;
import com.loginAPI.model.User;
import com.loginAPI.oauth.provider.FacebookUserInfo;
import com.loginAPI.oauth.provider.GoogleUserInfo;
import com.loginAPI.oauth.provider.NaverUserInfo;
import com.loginAPI.oauth.provider.SocialProviderUserInfo;
import com.loginAPI.properties.JwtProperties;
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
				
		postToFront(new JwtTokenFactory().getJwtToken(user));

		
		return super.loadUser(userRequest);
	}



	private void postToFront(Map<String, String> jwtToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken.get("jwtToken"));
		headers.add("RefreshToken", "RefreshToken "+jwtToken.get("refreshToken"));
		headers.setLocation(URI.create("http://127.0.0.1:5501/study_id_check.html"));
		new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}



	private User getUser(SocialProviderUserInfo socialProviderUserInfo) {
		String provider = socialProviderUserInfo.getProvider();
		String providerId = socialProviderUserInfo.getProviderId();
		String username = provider+"_"+providerId;
		
		User user = userRepository.findByUsername(username);
		
		if(user == null) {
			user = User.builder()
					.username(username)
					.email(socialProviderUserInfo.getEmail())
					.role("ROLE_USER")
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(user);
		}
		return user;
	}
	
}
