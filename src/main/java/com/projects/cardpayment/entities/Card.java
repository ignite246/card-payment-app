package com.projects.cardpayment.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name="card_details")
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cardId;

	@Column(unique = true, nullable = false)
	private Integer cardCVVNumber;

	@Column(nullable = false,length = 10, columnDefinition = "varchar(10) default '01/01/2024'")
	private String cardExpiryDate;

	@Column(unique = true, nullable = false, length = 10)
	private Integer cardHolderId;

	@Column(nullable = false, length = 20)
	private String cardHolderFirstName;

	@Column(nullable = false, length = 20)
	private String cardHolderLastName;

	@Column(nullable = false, columnDefinition = "integer default 0")
	private Integer cardBalance;

	@Column(nullable = false, length = 25)
	private String cardBankName;

	@Column(nullable = false)
	@CreationTimestamp
	private Date cardCreatedOn;

	@Column(nullable = false)
	@UpdateTimestamp
	private Date cardLastUpdatedOn;

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

	public Date getCardCreatedOn() {
		return cardCreatedOn;
	}

	public void setCardCreatedOn(Date cardCreatedOn) {
		this.cardCreatedOn = cardCreatedOn;
	}

	public Date getCardLastUpdatedOn() {
		return cardLastUpdatedOn;
	}

	public void setCardLastUpdatedOn(Date cardLastUpdatedOn) {
		this.cardLastUpdatedOn = cardLastUpdatedOn;
	}

	@Override
	public String toString() {
		return "Card [" +
				"cardId=" + cardId +
				", cardCVVNumber=" + cardCVVNumber +
				", cardExpiryDate='" + cardExpiryDate + '\'' +
				", cardHolderId=" + cardHolderId +
				", cardHolderFirstName='" + cardHolderFirstName + '\'' +
				", cardHolderLastName='" + cardHolderLastName + '\'' +
				", cardBalance=" + cardBalance +
				", cardBankName='" + cardBankName + '\'' +
				", cardCreatedOn=" + cardCreatedOn +
				", cardLastUpdatedOn=" + cardLastUpdatedOn +
				']';
	}
}
