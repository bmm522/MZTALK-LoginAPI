package com.loginAPI.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loginAPI.repository.UserRepository;

@Service
public class UserNameDuplicateCheckServiceImpl implements UserNameDuplicateCheckService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Map<String, Object> duplicateCheck(String username) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(userRepository.existsByUsername(username)) {
			map.put("duplicateCheckResult", "exists");
			return map;
		}
		map.put("duplicateCheckResult", "available");
		return map;
	}
}
