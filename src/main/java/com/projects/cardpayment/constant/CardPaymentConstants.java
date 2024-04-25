package com.projects.cardpayment.constant;

/**
 * Application constants
 */
public class CardPaymentConstants {
	
	
	/************************ API Response elated constants **************************/
	
	public static final String STATUS = "status";
	public static final String STATUS_MSG = "statusMessage";
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Failure";
	public static final String STATUS_CODE = "statusCode";
	public static final String SUCCESS_STATUS_200="200";
	public static final String FAILURE_STATUS_500="500";
	public static final String REASON = "reason";
	
	/************************ Money Transaction related constants **************************/
	
	public static final String SELF_WITHDRAWAL = "SELF_WITHDRAWAL";
	public static final String SELF_DEPOSIT = "SELF_DEPOSIT";
	public static final String CARD_2_CARD_MONEY_TRANSFER = "CARD_2_CARD_MONEY_TRANSFER";
	public static final String ORDER_PAYMENT = "ORDER_PAYMENT";
	

	/************************ API URL related constants **************************/

	public static final String TXN_API = "/txn-api";
	public static final String FIND_TXN_DETAILS_BY_CARD_ID = "/find-txnDetails-by-id";

}
