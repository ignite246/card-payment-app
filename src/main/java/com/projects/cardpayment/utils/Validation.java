package com.projects.cardpayment.utils;

import com.projects.cardpayment.entities.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

        return validationPassed;
    }
}
