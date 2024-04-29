package com.projects.cardpayment.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.cardpayment.entities.User;
import com.projects.cardpayment.request.DTO.LoginRequestDTO;
import com.projects.cardpayment.response.dto.UserLoginResponseDTO;
import com.projects.cardpayment.service.UserAppService;
import com.projects.cardpayment.utils.Validation;

@RestController
@RequestMapping("/operation-api")

public class OperationController {

	Logger logger = LoggerFactory.getLogger(OperationController.class);

	@Autowired
	private UserAppService userAppService;

	@Autowired
	private Validation validation;

	@PostMapping("/create-AppUser")
	public String createAppUser(@RequestBody User userApp) {
		final boolean userAppValidation = validation.userValidation(userApp);
		String response = null;
		try {
			if (userAppValidation) {
				User savedUserApp = userAppService.createAppUser(userApp);
				Integer id = savedUserApp.getId();
				response = "User Created Successfully With ID :: " + id;
			} else {
				response = "Unable to create user, validation failed";
			}
		} catch (Exception e) {
			response = e.getMessage();
		}
		return response;
	}

	@PostMapping("/user-Login")
	public UserLoginResponseDTO userLogin(@RequestHeader("userName")String userName, @RequestHeader("password") String password) {

		try {
		//	String userName = loginRequestDTO.getUserName();
		//	String password = loginRequestDTO.getPassword();
			logger.info("username = " + userName);
			logger.info("password = " + password);
//			logger.info("requestDTO= " + loginRequestDTO);
			User user = userAppService.userLoginService(userName, password);
			logger.info("here is data " + user);
			if (user != null) {
				UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
				userLoginResponseDTO.setResponseCode("200");
				userLoginResponseDTO.setResponseMsg("login Success");
				userLoginResponseDTO.setRole(user.getRole());

				return userLoginResponseDTO;
			}

			else {
				UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
				userLoginResponseDTO.setResponseCode("201");
				userLoginResponseDTO.setResponseMsg("login Failed");
				userLoginResponseDTO.setRole(" ");
				return userLoginResponseDTO;

			}
		} catch (Exception e) {
			UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
			userLoginResponseDTO.setResponseCode("401");
			userLoginResponseDTO.setResponseMsg(e.getMessage());
			userLoginResponseDTO.setRole(" ");
			return userLoginResponseDTO;

		}

	}
	
	@GetMapping("/user")
	public String user(@RequestHeader("userName") String userName ) {
		return userName;
		
	}

}
