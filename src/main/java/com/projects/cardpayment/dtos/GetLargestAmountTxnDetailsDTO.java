package com.projects.cardpayment.dtos;

import java.util.List;

import com.projects.cardpayment.entities.TxnDetails;

public class GetLargestAmountTxnDetailsDTO {
	
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
		return "GetLargestAmountTxnDetailsDTO [status=" + status + ", statusCode=" + statusCode + ", txnDetails="
				+ txnDetails + "]";
	}
	public GetLargestAmountTxnDetailsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GetLargestAmountTxnDetailsDTO(String status, String statusCode, TxnDetails txnDetails) {
		super();
		this.status = status;
		this.statusCode = statusCode;
		this.txnDetails = txnDetails;
	}
	
	
	

}
