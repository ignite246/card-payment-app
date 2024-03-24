package com.projects.cardpayment.dtos;

import com.projects.cardpayment.entities.Card;

public class FindACardByIdResponseDTO {
    private String statusMessage;
    private Integer statusCode;
    private Card card;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public String toString() {
        return "FindACardByIdResponseDTO{" +
                "statusMessage='" + statusMessage + '\'' +
                ", statusCode=" + statusCode +
                ", card=" + card +
                '}';
    }
}
