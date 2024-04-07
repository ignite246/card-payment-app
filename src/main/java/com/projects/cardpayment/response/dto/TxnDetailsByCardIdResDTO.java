package com.projects.cardpayment.response.dto;

import java.util.List;

import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.TxnDetails;

public class TxnDetailsByCardIdResDTO {
	
	private String status;//Success,Failure
	private String statusCode;//Invalid-401,ServiceDown-402
	private List<TxnDetails> txnList;
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
	public List<TxnDetails> getTxnList() {
		return txnList;
	}
	public void setTxnList(List<TxnDetails> txnList) {
		this.txnList = txnList;
	}
	@Override
	public String toString() {
		return "TxnDetailsByCardIdResDTO [status=" + status + ", statusCode=" + statusCode + ", txnList=" + txnList
				+ "]";
	}
	public TxnDetailsByCardIdResDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TxnDetailsByCardIdResDTO(String status, String statusCode, List<TxnDetails> txnList) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.txnList = txnList;
	}

	
}
