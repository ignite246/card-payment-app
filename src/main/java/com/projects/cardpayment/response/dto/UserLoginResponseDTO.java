package com.projects.cardpayment.response.dto;

public class UserLoginResponseDTO {

	
	
	private String responseMsg;
	private String responseCode;
	private String role;
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserLoginResponseDTO [responseMsg=" + responseMsg + ", responseCode=" + responseCode + ", role=" + role
				+ "]";
	}
	public UserLoginResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserLoginResponseDTO(String responseMsg, String responseCode, String role) {
		super();
		this.responseMsg = responseMsg;
		this.responseCode = responseCode;
		this.role = role;
	}
	
	
	
}
