package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.user.binding.ActivateAccount;
import com.user.binding.Login;
import com.user.binding.User;
import com.user.service.UserMgmtServiceImpl;

@RestController
public class UserMasterRestController {
	
	@Autowired
	private UserMgmtServiceImpl serviceImpl;
	
	@PostMapping("/user")
	public ResponseEntity<String> saveUser(@RequestBody User user) {
		boolean isSaved = serviceImpl.saveUser(user);
		if(isSaved) {
			return new ResponseEntity<>("User registered successfully. Please check your email for login details.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("User registration failed.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PostMapping("/activate")
	public ResponseEntity<String> activateUserAcc(@RequestBody ActivateAccount activateAccount) {
		boolean isActivated = serviceImpl.activateUserAcc(activateAccount);
		if(isActivated) {
			return new ResponseEntity<>("Account Activated", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Invalid Temporary Password", HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUser() {
		List<User> allUsers = serviceImpl.getAllUser();
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
		User user = serviceImpl.getUserById(userId);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer userId) {
		boolean isDelete = serviceImpl.deleteByUserId(userId);
		
		if(isDelete) {
			return new ResponseEntity<>("Deleted", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/status/{userId}/{status}")
	public ResponseEntity<String> statusChange(@PathVariable Integer userId, @PathVariable String status) {
		boolean ifChanged = serviceImpl.changeAccountStatus(userId, status);
		
		if(ifChanged) {
			return new ResponseEntity<>("Status Changed", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Failed To Changed", HttpStatus.OK);
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login login) {
		return new ResponseEntity<>(serviceImpl.login(login), HttpStatus.OK);
	}
	
	@PostMapping("/forgotPwd/{email}")
	public ResponseEntity<String> forgotPassword(@PathVariable String email) {
		return new ResponseEntity<>(serviceImpl.forgotPassword(email), HttpStatus.OK);
	}
}
