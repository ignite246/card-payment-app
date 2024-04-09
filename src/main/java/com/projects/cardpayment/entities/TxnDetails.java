package com.projects.cardpayment.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="txn_details")
public class TxnDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(nullable = true)
	private Integer senderId; //cardId of the sender
	
	@Column(nullable = true)
	private String senderName;
	
	@Column(nullable = true)
	private Integer receiverId;
	
	@Column(nullable = true)
	private String receiverName;
	
	@Column(nullable = false)
	private Integer txnAmount;
	
	@Column(nullable = false)
	private Date txnDate;
	
	@Column(nullable = false)
	private String purpose;
	
	@Column(nullable=false)
	private String txnUUID;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public Integer getTxnAmount() {
		return txnAmount;
	}

	public void setTxnAmount(Integer txnAmount) {
		this.txnAmount = txnAmount;
	}

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getTxnUUID() {
		return txnUUID;
	}

	public void setTxnUUID(String txnUUID) {
		this.txnUUID = txnUUID;
	}

	@Override
	public String toString() {
		return "TxnDetails [id=" + id + ", senderId=" + senderId + ", senderName=" + senderName + ", receiverId="
				+ receiverId + ", receiverName=" + receiverName + ", txnAmount=" + txnAmount + ", txnDate=" + txnDate
				+ ", purpose=" + purpose + ", txnUUID=" + txnUUID + "]";
	}

	public TxnDetails() {
	}

	public TxnDetails(Integer id, Integer senderId, String senderName, Integer receiverId, String receiverName,
			Integer txnAmount, Date txnDate, String purpose, String txnUUID) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.senderName = senderName;
		this.receiverId = receiverId;
		this.receiverName = receiverName;
		this.txnAmount = txnAmount;
		this.txnDate = txnDate;
		this.purpose = purpose;
		this.txnUUID = txnUUID;
	}
	
	
	
	}	