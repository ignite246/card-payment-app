package com.projects.cardpayment.response.dto;

import com.projects.cardpayment.entities.TxnDetails;

public class GetSmallestAmountTxnDetailsResDTO {

	private String status;//Success,Failure
	private String statusCode;
	private TxnDetails txnDetails;
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
	public TxnDetails getTxnDetails() {
		return txnDetails;
	}
	public void setTxnDetails(TxnDetails txnDetails) {
		this.txnDetails = txnDetails;
	}
	@Override
	public String toString() {
		return "GetSmallestAmountTxnDetailsDTO [status=" + status + ", statusCode=" + statusCode + ", txnDetails="
				+ txnDetails + "]";
	}
	public GetSmallestAmountTxnDetailsResDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GetSmallestAmountTxnDetailsResDTO(String status, String statusCode, TxnDetails txnDetails) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.txnDetails = txnDetails;
	}
	
	
	
}
