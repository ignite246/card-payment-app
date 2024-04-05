package com.projects.cardpayment.utils.mailservice;

/**
 * MailStructure represents a mail request body with subject line and email body.
 */

public class MailStructure {

    private String subject;
    private String message;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MailStructure{" +
                "subject='" + subject + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
