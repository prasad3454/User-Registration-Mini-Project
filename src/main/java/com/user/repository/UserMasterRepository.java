package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.entity.UserMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, Integer>{
	
	public UserMaster findByEmailAndPassword(String email, String password);
	
	public UserMaster findByEmail(String email);
}
