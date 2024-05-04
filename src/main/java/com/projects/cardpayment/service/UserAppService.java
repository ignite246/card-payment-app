package com.projects.cardpayment.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.cardpayment.entities.User;
import com.projects.cardpayment.repository.UserAppRepository;

@Service
public class UserAppService {
	Logger logger = LoggerFactory.getLogger(UserAppService.class);

	@Autowired
	private UserAppRepository userAppRepo;

	public User createAppUser(User userApp) {
		logger.info("CPA : CS : Saving card details {}", userApp);
		User savedUserDetails = null;

		try {
			savedUserDetails = userAppRepo.save(userApp);
		} catch (Exception e) {
			logger.error("Exception found at CPA : CS ", e);
		}
		return savedUserDetails;
	}

	public User userLoginService(String userName, String password) {
		return userAppRepo.findByUserNameAndPassword(userName, password);
	}
} 
