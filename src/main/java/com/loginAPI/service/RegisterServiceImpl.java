package com.loginAPI.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.loginAPI.model.User;
import com.loginAPI.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService{
	
	@Autowired
	public UserRepository userRepository;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;


	public void register(User user) {
		user.setRole("ROLE_USER");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		userRepository.save(user);

	}

}
