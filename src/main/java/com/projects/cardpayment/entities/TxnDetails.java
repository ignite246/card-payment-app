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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer txnId;
	
	@Column(nullable = false)
	private Integer senderId;
	
	@Column(nullable = false)
	private Integer receiverId;
	
	@Column(nullable = false)
	private Integer txnAmount;
	
	@Column(nullable = false)
	private Date txnDate;
	
	
	public Integer getTxnId() {
		return txnId;
	}
	public void setTxnId(Integer txnId) {
		this.txnId = txnId;
	}
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
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
	@Override
	public String toString() {
		return "TxnDetails [txnId=" + txnId + ", senderId=" + senderId + ", receiverId=" + receiverId + ", txnAmount="
				+ txnAmount + ", txnDate=" + txnDate + "]";
	}
}
