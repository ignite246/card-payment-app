package com.projects.cardpayment.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.cardpayment.controllers.TxnController;
import com.projects.cardpayment.dtos.GetLargestAmountTxnDetailsDTO;
import com.projects.cardpayment.entities.TxnDetails;
import com.projects.cardpayment.repository.TxnRepository;
import com.projects.cardpayment.response.dto.TxnDetailsByCardIdResDTO;

@Service
public class TxnService {
	
	Logger logger = LoggerFactory.getLogger(TxnController.class);
	
	@Autowired
	private TxnRepository txnRepo;
	
	
	public GetLargestAmountTxnDetailsDTO getTransactionDetailsOfTheLargestAmount(Integer cardId) {
		GetLargestAmountTxnDetailsDTO response = null;
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
		logger.info("largest amount txn detail {}",largestAmtTxn);

		response = new GetLargestAmountTxnDetailsDTO();
		if (largestAmtTxn == null ) {
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
		// TODO Auto-generated method stub
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
	
	

}
