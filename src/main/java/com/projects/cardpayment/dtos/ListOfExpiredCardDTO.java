package com.projects.cardpayment.dtos;

import java.util.List;

import com.projects.cardpayment.entities.Card;

public class ListOfExpiredCardDTO {
	
	private String status;//Success,Failure
	private String statusCode;//Invalid-401,ServiceDown-402
	private List<Card> cardList;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public List<Card> getCardList() {
		return cardList;
	}
	public void setCardList(List<Card> cardList) {
		this.cardList = cardList;
	}
	public ListOfExpiredCardDTO(String status, String statusCode, List<Card> cardList) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.cardList = cardList;
	}
	public ListOfExpiredCardDTO() {
		
	}
	
	

}
