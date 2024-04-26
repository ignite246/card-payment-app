package com.projects.cardpayment.utils.mailservice;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * MailService class has all the mail related features.
 * Sending mail to a single recipient with plain text as email body.
 * Sending mail to a single recipient with HTML content as email body.
 * Sending mail to a single recipient with attachment.
 * Mail also can be sent to multiple recipients(to)
 * Also cc and bcc can be mentioned
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
     * Send mail to a recipient with plain text as email body
     *
     * @param mailStructure   email request payload containing subject and text
     * @param recipientMailId single recipient email ID
     * @return mailResponse "SUCCESS" or "FAILURE"
     */
    public String sendEmail(MailStructure mailStructure, String recipientMailId) {
        final String methodName = "sendEmail";
        logger.info("== ClassName :: {}, MethodName :: {} STARTS ==", CLASS_NAME, methodName);
        String mailResponse;
        try {
            final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderMailUsername);
            simpleMailMessage.setSubject(mailStructure.getSubject().trim());
            simpleMailMessage.setText(mailStructure.getMessage().trim());
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

    /**
     * Send mail to a recipient with html content as email body
     *
     * @param mailStructure   consists of email subject and email body (HTML content)
     * @param recipientMailId to whom mail has to be sent
     * @return mailResponse SUCCESS or FAILURE
     */
    public String sendEmailWithHTMLContent(MailStructure mailStructure, String recipientMailId) {
        final String methodName = "sendEmail";
        logger.info("== ClassName :: {}, MethodName :: {} STARTS ==", CLASS_NAME, methodName);
        String mailResponse = "";
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(recipientMailId);
            mimeMessageHelper.setFrom(senderMailUsername);
            mimeMessageHelper.setSubject(mailStructure.getSubject().trim());
            mimeMessageHelper.setText(mailStructure.getMessage(), true);

            mailSender.send(mimeMessage);
            logger.info("Mail with HTML content has been sent successfully...");
            mailResponse = "SUCCESS";

        } catch (MessagingException e) {
            logger.error("Exception occurred while sending email with attachment::{}", e.getMessage());
            mailResponse = "FAILURE";
        }
        return mailResponse;
    }
}
