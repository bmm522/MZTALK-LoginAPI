package com.loginAPI.service;

import java.util.Map;

public interface UserNameDuplicateCheckService {
	
	public Map<String, Object> duplicateCheck(String username);
}
