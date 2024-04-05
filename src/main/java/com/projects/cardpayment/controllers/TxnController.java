package com.projects.cardpayment.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.cardpayment.dtos.GetLargestAmountTxnDetailsDTO;
import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.TxnDetails;
import com.projects.cardpayment.repository.TxnRepository;
import com.projects.cardpayment.response.dto.TxnDetailsByCardIdResDTO;
import com.projects.cardpayment.service.TxnService;

@RestController
@RequestMapping("/txn-api")
public class TxnController {
	Logger logger = LoggerFactory.getLogger(TxnController.class);

	@Autowired
	private TxnRepository txnRepo;

	@Autowired
	private TxnService txnService;

	@GetMapping("/find-txnDetails-by-id/{cardId}")
	public TxnDetailsByCardIdResDTO findTxnDetailsByCardId(@PathVariable("cardId") Integer cardId) {
		logger.info("====== findtxnDetailsById :: Input	 Received ====");

		logger.info("Card ID :: {}", cardId);

		TxnDetailsByCardIdResDTO response = null;
		TxnDetailsByCardIdResDTO txnDetailsByCardId = null;

		if (cardId > 0 && cardId != null) {
			txnDetailsByCardId = txnService.findTxnDetailsByCardId(cardId);
		}
		return txnDetailsByCardId;
	}

	@GetMapping("Get-txnDetails-of-largestAmount-by-cardId/{cardId}")
	public GetLargestAmountTxnDetailsDTO getTransactionDetailsOfTheLargestAmount(
			@PathVariable("cardId") Integer cardId) {
		GetLargestAmountTxnDetailsDTO txnDetail = null;
		if (cardId > 0 && cardId != null) {

			txnDetail = txnService.getTransactionDetailsOfTheLargestAmount(cardId);
		}

		return txnDetail;

	}
}
