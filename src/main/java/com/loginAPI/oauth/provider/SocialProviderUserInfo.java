package com.loginAPI.oauth.provider;

public interface SocialProviderUserInfo {

	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
}
