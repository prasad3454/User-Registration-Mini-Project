package com.user.service;

import java.util.List;

import com.user.binding.ActivateAccount;
import com.user.binding.Login;
import com.user.binding.User;

public interface UserMgmtService {
	
	public boolean saveUser(User user);
	
	public boolean activateUserAcc(ActivateAccount activateAcc);
	
	public List<User> getAllUser();
	
	public User getUserById(Integer userId);
	
	public boolean deleteByUserId(Integer userId);
	
	public boolean changeAccountStatus(Integer userId, String status);
	
	public String login(Login login);
	
	public String forgotPassword(String email);
}
