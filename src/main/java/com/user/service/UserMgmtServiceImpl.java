package com.user.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.user.binding.ActivateAccount;
import com.user.binding.Login;
import com.user.binding.User;
import com.user.entity.UserMaster;
import com.user.repository.UserMasterRepository;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {

	@Autowired
	private UserMasterRepository masterRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public boolean saveUser(User user) {

		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);

		entity.setPassword(generateRandomPwd());
		String tempPassword = generateRandomPwd();
		entity.setAccStatus("In-Active");

		UserMaster save = masterRepo.save(entity);
		
		String fullName = user.getFullName();
		String url= "";

		if (save.getUserId() != null) {
			sendRegistrationEmail(user.getEmail(), fullName, url, tempPassword);
			return true;
		}

		return false;
	}

	@Override
	public boolean activateUserAcc(ActivateAccount activateAcc) {

		UserMaster entity = new UserMaster();
		entity.setEmail(activateAcc.getEmail());
		entity.setPassword(activateAcc.getTempPwd());

		Example<UserMaster> of = Example.of(entity);

		List<UserMaster> findAll = masterRepo.findAll(of);

		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setPassword(activateAcc.getNewPwd());
			userMaster.setAccStatus("Active");
			masterRepo.save(userMaster);
			return true;
		}
	}

	@Override
	public List<User> getAllUser() {

		List<UserMaster> findAll = masterRepo.findAll();

		List<User> users = new ArrayList<>();

		for (UserMaster entity : findAll) {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public User getUserById(Integer userId) {

		Optional<UserMaster> findById = masterRepo.findById(userId);

		if (findById.isPresent()) {
			User user = new User();
			UserMaster userMaster = findById.get();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}

		return null;
	}

	@Override
	public boolean deleteByUserId(Integer userId) {

		try {
			masterRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String status) {

		Optional<UserMaster> findById = masterRepo.findById(userId);

		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(status);
			masterRepo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {

		UserMaster entity = masterRepo.findByEmailAndPassword(login.getEmail(), login.getPassword());
		if (entity == null) {
			return "Invalid Credentials";
		}
		if (entity.getAccStatus().equals("Active")) {
			return "Success";
		} else {
			return "Account is not Activated";
		}
	}

	@Override
	public String forgotPassword(String email) {

		UserMaster entity = masterRepo.findByEmail(email);

		if (entity == null) {
			return "Invalid Email";
		}
		sendPasswordEmail(email, entity.getPassword());
		return "Password Sent to your Email";
	}

	private String generateRandomPwd() {

		String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";

		String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

		StringBuilder sb = new StringBuilder();

		Random random = new Random();

		int length = 6;

		for (int i = 0; i < length; i++) {

			int index = random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}

		return sb.toString();

	}

	private void sendRegistrationEmail(String email, String fullName, String url, String tempPassword) {

		try {
			
			String subject = "Your Account Registration";
			String fileName = "Reg-Email-Body.txt";
			String body = readRegEmailBody(fullName, tempPassword, url, fileName);
			
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

			messageHelper.setFrom("taraprasadjena40@gmail.com");
			messageHelper.setTo(email);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true); 

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			System.out.println("Error while sending mail: " + e.getMessage());
		}
	}

	private void sendPasswordEmail(String email, String password) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

			String subject = "Your Password";
			String fileName = "Recover-Pwd-Body.txt";
			String body = readRegEmailBody("", password, "", fileName);

			messageHelper.setFrom("taraprasadjena40@gmail.com");
			messageHelper.setTo(email);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true); 

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			System.out.println("Error while sending mail: " + e.getMessage());
		}
	}
	
	private String readRegEmailBody(String fullName, String pwd, String url, String fileName) {
//		String fileName= "Reg-Email-Body.txt";
		String mailBody = null;
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			StringBuffer sb = new StringBuffer();
			
			String line = br.readLine();
			while(line != null) {
				sb.append(line);
				line = br.readLine();
			}
			
			br.close();
			
			mailBody = sb.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullName);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("{PWD}", pwd);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return mailBody;
	}
}
