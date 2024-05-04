package com.projects.cardpayment.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.projects.cardpayment.constant.CardPaymentConstants;
import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.User;

@Component
public class Validation {

	Logger logger = LoggerFactory.getLogger(Validation.class);

	public boolean createCardRequestValidation(Card card) {
		boolean validationPassed = true;
		if (!isFirstnameLastnameBanknamePatternValid(card.getCardHolderFirstName())) {
			logger.info("Card Holder FirstName Validation Result :: FAILED");
			validationPassed = false;
		}
		if (!isFirstnameLastnameBanknamePatternValid(card.getCardHolderLastName())) {
			logger.info("Card Holder LastName Validation Result :: FAILED");
			validationPassed = false;
		}
		if (!isFirstnameLastnameBanknamePatternValid(card.getCardBankName())) {
			logger.info("Card bankName Validation Result :: FAILED {}", card.getCardBankName());
			validationPassed = false;
		}
		if (Objects.isNull(card.getCardBalance()) || card.getCardBalance() <= 0) {
			logger.info("Card balance validation failed {}", card.getCardBalance());
			validationPassed = false;
		}
		if (Objects.isNull(card.getCardCVVNumber()) || String.valueOf(card.getCardCVVNumber()).length() != 4) {
			logger.info("Card cvvNumber validation failed {}", card.getCardCVVNumber());
			validationPassed = false;
		}
		if (Objects.isNull(card.getCardHolderId()) || String.valueOf(card.getCardHolderId()).length() != 6) {
			logger.info("Card cardHolderId validation failed {}", card.getCardHolderId());
			validationPassed = false;
		}
		if (Objects.isNull(card.getCardExpiryDate()) || card.getCardExpiryDate().trim().length() != 10) {
			logger.info("Card expirydate validation failed {}", card.getCardExpiryDate());
			validationPassed = false;
		}
		if (Objects.isNull(card.getEmail()) || card.getEmail().trim().length() < 5) {
			logger.info("Card Email Validation isNULL :: FAILED");
			validationPassed = false;
		}
		if (!isEmailPatternValid(card.getEmail())) {
			logger.info("Card email pattern is not matching :: VALIDATION FAILED");
			validationPassed = false;
		}
		if (!isExpiryDatePatternValid(card.getCardExpiryDate())) {
			logger.info("Card ExpiryDate pattern is not matching :: VALIDATION FAILED");
			validationPassed = false;
		}
		return validationPassed;
	}

	/**
	 * @param String : emailId
	 * @return Boolean : TRUE or FALSE if email pattern is valid
	 */
	public boolean isEmailPatternValid(String emailId) {
		boolean isEmailValid = false;
		String regex = "[^@]{5,20}[@][A-z]+[.][a-z]{3}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(emailId);

		if (matcher.matches()) {
			logger.info("Card Email Validation Result :: SUCCESS");
			isEmailValid = true;
		} else {
			logger.info("Card Email Validation Result :: FAILED");
			isEmailValid = false;
		}
		return isEmailValid;
	}

	/**
	 * @param String : cardExpiryDate
	 * @return Boolean : TRUE or FALSE if cardExpiryDate pattern(12/04/2024) is
	 *         valid
	 */
	public boolean isExpiryDatePatternValid(String expiryDate) {
		logger.info("Expiry Date To Be Validated :: {}", expiryDate);
		String regex = "[\\d]{2}[\\/][\\d]{2}[\\/][\\d]{4}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(expiryDate);
		return matcher.matches();
	}

	public boolean isFirstnameLastnameBanknamePatternValid(String name) {
		logger.info("Firstname lastname bankname To Be Validated :: {}", name);
		String regex = "[A-z]{3,20}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	public boolean isPasswordValid(String password) {
		logger.info("Password To Be Validated :: {}", password);
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%])[A-z0-9@#$%]{8,20}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	/*
	 * String regex = "[A-z]{3,20}"; Pattern pattern = Pattern.compile(regex);
	 * Matcher matcher = pattern.matcher(role);
	 * 
	 * return matcher.matches();
	 */
	public boolean isRoleValid(String role) {
		logger.info("Role To Be Validated :: {}", role);
		return role.equalsIgnoreCase(CardPaymentConstants.ADMIN)
				|| role.equalsIgnoreCase(CardPaymentConstants.NONADMIN);
	}

	public boolean userValidation(User userApp) {
		boolean validationPassed = true;

		if (!isEmailPatternValid(userApp.getUserName())) {
			logger.info("userApp email pattern is not matching :: VALIDATION FAILED");
			validationPassed = false;
		}

		if (!isPasswordValid(userApp.getPassword())) {
			logger.info("userApp password is not matching :: VALIDATION FAILED");
			validationPassed = false;
		}

		if (!isRoleValid(userApp.getRole())) {
			logger.info("userApp role is not matching :: VALIDATION FAILED");
			validationPassed = false;
		}

		return validationPassed;
	}

}
