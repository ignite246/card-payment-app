package com.projects.cardpayment.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.projects.cardpayment.entities.Card;

@Component
public class Validation {

	Logger logger = LoggerFactory.getLogger(Validation.class);

	public boolean createCardRequestValidation(Card card) {
		boolean validationPassed = true;

		if (Objects.isNull(card.getCardHolderFirstName()) || card.getCardHolderFirstName().trim().length() < 3) {
			logger.info("Card Holder FirstName Validation Result :: FAILED");
			validationPassed = false;
		}

		if (Objects.isNull(card.getCardHolderLastName()) || card.getCardHolderLastName().trim().length() < 3) {
			logger.info("Card Holder LastName Validation Result :: FAILED");
			validationPassed = false;
		}

		if (Objects.isNull(card.getEmail())) {
			logger.info("Card Email Validation isNULL :: FAILED");
			validationPassed = false;

		} else {
			String email = card.getEmail();
			String regex = "[^@]{5,20}[@][A-z]+[.][a-z]{3}";

			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(email);

			if (matcher.matches()) {
				logger.info("Card Email Validation Result :: SUCCESS");
				validationPassed = true;
			} else {
				logger.info(" Email Validation :: FAILED");
				validationPassed = false;
			}
		}
		return validationPassed;
	}
}
