package com.projects.cardpayment.utils.mailservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MailService class has all the mail related features
 * Sending mail to single recipient with or without attachment
 * Also cc and bcc can be mentioned
 * Sending mail to multiple recipients at a time
 */
@Service
public class MailService {

    private final String CLASS_NAME = "MailService";
    Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderMailUsername;

    /**
     * @param mailStructure   email request payload containing subject and text
     * @param recipientMailId single recipient email ID
     * @return "SUCCESS" or "FAILURE"
     */
    public String sendEmail(MailStructure mailStructure, String recipientMailId) {
        final String methodName = "sendEmail";
        logger.info("== ClassName :: {}, MethodName :: {} STARTS ==", CLASS_NAME, methodName);
        String mailResponse;
        try {
            final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderMailUsername);
            simpleMailMessage.setSubject(mailStructure.getSubject());
            simpleMailMessage.setText(mailStructure.getMessage());
            simpleMailMessage.setTo(recipientMailId);
            mailSender.send(simpleMailMessage);
            mailResponse = "SUCCESS";
        } catch (Exception ex) {
            logger.error("Exception occurred while sending mail to :: {}", recipientMailId);
            logger.error("Exception reason :: {}", ex.getMessage());
            mailResponse = "FAILURE";
        }
        logger.info("*** ClassName :: {}, MethodName :: {} ENDS ***", CLASS_NAME, methodName);
        return mailResponse;
    }

    public String sendEmail(MailStructure mailStructure, List<String> recipientMailIdList){
        final String methodName = "sendEmail";
        logger.info("== ClassName :: {}, MethodName :: {} STARTS ==", CLASS_NAME, methodName);

        return "FAILURE";
    }
}
