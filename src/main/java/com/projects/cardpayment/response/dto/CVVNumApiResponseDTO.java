package com.projects.cardpayment.response.dto;

public class CVVNumApiResponseDTO {

	private String statusMessage;
	private Integer statusCode;
	private Integer cardCVVNum;
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
	public Integer getCardCVVNum() {
		return cardCVVNum;
	}
	public void setCardCVVNum(Integer cardCVVNum) {
		this.cardCVVNum = cardCVVNum;
	}
	@Override
	public String toString() {
		return "CVVNumApiResponseDTO [statusMessage=" + statusMessage + ", statusCode=" + statusCode + ", cardCVVNum="
				+ cardCVVNum + "]";
	}
	
	
	
}
