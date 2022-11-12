package com.loginAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.loginAPI.model.User;

public interface UserRepository extends JpaRepository<User,Integer>{

    public User findById(long id);

    public User findByUsername(String username);

    
	public boolean existsByUsername(String username);
	
	
	


}
