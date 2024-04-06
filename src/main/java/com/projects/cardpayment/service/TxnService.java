package com.projects.cardpayment.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.cardpayment.entities.TxnDetails;
import com.projects.cardpayment.repository.TxnRepository;
import com.projects.cardpayment.response.dto.GetLargestAmountTxnDetailsResDTO;
import com.projects.cardpayment.response.dto.GetSmallestAmountTxnDetailsResDTO;
import com.projects.cardpayment.response.dto.TxnDetailsByCardIdResDTO;

@Service
public class TxnService {

	Logger logger = LoggerFactory.getLogger(TxnService.class);

	@Autowired
	private TxnRepository txnRepo;

	public GetLargestAmountTxnDetailsResDTO getTransactionDetailsOfTheLargestAmount(Integer cardId) {
		GetLargestAmountTxnDetailsResDTO response = null;
		TxnDetails txnDetails2 = null;
		TxnDetails largestAmtTxn = null;
		Integer txnAmount = null;
		List<TxnDetails> txnDetailsList = txnRepo.findBySenderId(cardId);
		logger.info("transaction details {}", txnDetailsList);
		Integer largestAmount = 0;

		for (int i = 0; i < txnDetailsList.size(); i++) {

			txnDetails2 = txnDetailsList.get(i);
			txnAmount = txnDetails2.getTxnAmount();

			if (txnAmount > largestAmount) {
				largestAmount = txnAmount;
				largestAmtTxn = txnDetails2;
			}
		}
		logger.info("txnL== amount {}", largestAmount);
		logger.info("largest amount txn detail {}", largestAmtTxn);

		response = new GetLargestAmountTxnDetailsResDTO();
		if (largestAmtTxn == null) {
			response.setStatus("Failure");
			response.setStatusCode("7000");// frontend team will have mapping as No Card Found
		} else {
			response.setStatus("Success");
			response.setStatusCode("8000");
			response.setTxnDetails(largestAmtTxn);
		}

		return response;

	}

	public TxnDetailsByCardIdResDTO findTxnDetailsByCardId(Integer cardId) {
		TxnDetailsByCardIdResDTO response = null;

		try {
			List<TxnDetails> txnDetails = txnRepo.findBySenderId(cardId);
			logger.info("transaction details {}", txnDetails);
			response = new TxnDetailsByCardIdResDTO();
			response.setStatus("Success");
			response.setStatusCode("7000");
			response.setTxnList(txnDetails);
		} catch (Exception e) {
			logger.error("Exception occured while fetching txnn details using cardid {}", e);
			response = new TxnDetailsByCardIdResDTO();
			response.setStatus("Failed");
			response.setStatusCode("8000");
			response.setTxnList(null);

		}
		return response;
	}

	public GetSmallestAmountTxnDetailsResDTO getTransactionDetailsOfTheSmallestAmount(Integer cardId) {
		GetSmallestAmountTxnDetailsResDTO response = null;
		TxnDetails currentTxnDetails = null;
		TxnDetails smallestAmtTxnDetails = null;
		Integer txnAmount = null;
		String status = null;
		String code = null;
		try {
			status = "failure";
			code = "7000";

			List<TxnDetails> txnDetailsList = txnRepo.findBySenderId(cardId);
			logger.info("TS:transaction details {}", txnDetailsList);
			
			if(!(txnDetailsList ==null || txnDetailsList.size()<=0))
{
			TxnDetails firstTxnDetails = txnDetailsList.get(0); // fetching txn amount of first txn details
			Integer smallestAmount = firstTxnDetails.getTxnAmount();
			smallestAmtTxnDetails = firstTxnDetails;

			for (int i = 1; i < txnDetailsList.size(); i++) {
				currentTxnDetails = txnDetailsList.get(i);
				txnAmount = currentTxnDetails.getTxnAmount();
				logger.info("TS:txnAmount {} smallestAmt {} ", txnAmount, smallestAmount);
				if (txnAmount < smallestAmount) {
					smallestAmount = txnAmount;
					smallestAmtTxnDetails = currentTxnDetails;
					logger.info("TS:smallestAmtTxnDetails {}  ", smallestAmtTxnDetails);
					logger.info("TS:currentTxnDetails {} ", currentTxnDetails);
				}
			}
			logger.info("TS:txnL== amount {}", smallestAmount);
			logger.info("TS:smallest amount txn detail {}", smallestAmtTxnDetails);
			status = "success";
			code = "8000";
}
			else {
				status="CARD IS ABSENT";
			}
		} catch (Exception e) {
			status = e.getMessage();
			logger.error("TS:EXCEPTION {}", e.getMessage());
		}
		response = new GetSmallestAmountTxnDetailsResDTO();
		response.setTxnDetails(smallestAmtTxnDetails);
		response.setStatus(status);
		response.setStatusCode(code);
		return response;
	}
}
