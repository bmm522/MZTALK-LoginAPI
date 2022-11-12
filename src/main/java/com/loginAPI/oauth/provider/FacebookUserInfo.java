package com.loginAPI.oauth.provider;

import java.util.Map;

public class FacebookUserInfo implements SocialProviderUserInfo{

	
	private Map<String, Object> attributes; // oauth2User.getAttibuutes;
	
	
	public FacebookUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String getProviderId() {
		return (String)attributes.get("id");
	}

	@Override
	public String getProvider() {
		return "facebook";
	}

	@Override
	public String getEmail() {
		return(String)attributes.get("email");
	}

	@Override
	public String getName() {
		return(String)attributes.get("name");
	}	
}
