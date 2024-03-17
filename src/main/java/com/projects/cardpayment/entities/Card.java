package com.projects.cardpayment.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="card_details")
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cardId;
	private Integer cardCVVNumber;
	private String cardExpiryDate;
	private Integer cardHolderId;
	private String cardHolderFirstName;
	private String cardHolderLastName;
	private Integer cardBalance;
	private String cardBankName;
	public Card() {
		super();
		
	}
	public Card(Integer cardId, Integer cardCVVNumber, String cardExpiryDate, Integer cardHolderId,
			String cardHolderFirstName, String cardHolderLastName, Integer cardBalance, String cardBankName) {
		super();
		this.cardId = cardId;
		this.cardCVVNumber = cardCVVNumber;
		this.cardExpiryDate = cardExpiryDate;
		this.cardHolderId = cardHolderId;
		this.cardHolderFirstName = cardHolderFirstName;
		this.cardHolderLastName = cardHolderLastName;
		this.cardBalance = cardBalance;
		this.cardBankName = cardBankName;
	}
	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	public Integer getCardCVVNumber() {
		return cardCVVNumber;
	}
	public void setCardCVVNumber(Integer cardCVVNumber) {
		this.cardCVVNumber = cardCVVNumber;
	}
	public String getCardExpiryDate() {
		return cardExpiryDate;
	}
	public void setCardExpiryDate(String cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}
	public Integer getCardHolderId() {
		return cardHolderId;
	}
	public void setCardHolderId(Integer cardHolderId) {
		this.cardHolderId = cardHolderId;
	}
	public String getCardHolderFirstName() {
		return cardHolderFirstName;
	}
	public void setCardHolderFirstName(String cardHolderFirstName) {
		this.cardHolderFirstName = cardHolderFirstName;
	}
	public String getCardHolderLastName() {
		return cardHolderLastName;
	}
	public void setCardHolderLastName(String cardHolderLastName) {
		this.cardHolderLastName = cardHolderLastName;
	}
	public Integer getCardBalance() {
		return cardBalance;
	}
	public void setCardBalance(Integer cardBalance) {
		this.cardBalance = cardBalance;
	}
	public String getCardBankName() {
		return cardBankName;
	}
	public void setCardBankName(String cardBankName) {
		this.cardBankName = cardBankName;
	}
	@Override
	public String toString() {
		return "Card [cardId=" + cardId + ", cardCVVNumber=" + cardCVVNumber + ", cardExpiryDate=" + cardExpiryDate
				+ ", cardHolderId=" + cardHolderId + ", cardHolderFirstName=" + cardHolderFirstName
				+ ", cardHolderLastName=" + cardHolderLastName + ", cardBalance=" + cardBalance + ", cardBankName="
				+ cardBankName + "]";
	}

}
