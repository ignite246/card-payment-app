package com.projects.cardpayment.controllers;

import com.projects.cardpayment.constant.CardPaymentConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.cardpayment.repository.TxnRepository;
import com.projects.cardpayment.response.dto.GetLargestAmountTxnDetailsResDTO;
import com.projects.cardpayment.response.dto.GetSmallestAmountTxnDetailsResDTO;
import com.projects.cardpayment.response.dto.TxnDetailsByCardIdResDTO;
import com.projects.cardpayment.service.TxnService;

@RestController
@RequestMapping(CardPaymentConstants.TXN_API)
public class TxnController {
    Logger logger = LoggerFactory.getLogger(TxnController.class);

    @Autowired
    private TxnRepository txnRepo;

    @Autowired
    private TxnService txnService;

    @GetMapping(CardPaymentConstants.FIND_TXN_DETAILS_BY_CARD_ID+"/{cardId}")
    public TxnDetailsByCardIdResDTO findTxnDetailsByCardId(@PathVariable("cardId") Integer cardId) {
        logger.info("====== findTxnDetailsByCardId :: Input	 Received ====");
        logger.info("Card ID :: {}", cardId);

        TxnDetailsByCardIdResDTO txnDetailsByCardId = null;
        if (cardId != null && cardId > 0) {
            txnDetailsByCardId = txnService.findTxnDetailsByCardId(cardId);
        }
        return txnDetailsByCardId;
    }

    @GetMapping("Get-txnDetails-of-largestAmount-by-cardId/{cardId}")
    public GetLargestAmountTxnDetailsResDTO getTransactionDetailsOfTheLargestAmount(
            @PathVariable("cardId") Integer cardId) {
        logger.info("====== getTransactionDetailsOfTheLargestAmount :: Input Received ====");
        logger.info("Card ID :: {}", cardId);
        GetLargestAmountTxnDetailsResDTO txnDetail = null;
        if (cardId != null && cardId > 0) {
            txnDetail = txnService.getTransactionDetailsOfTheLargestAmount(cardId);
        }
        return txnDetail;
    }

    @GetMapping("Get-txnDetails-of-smallest-by-cardId/{cardId}")
    public GetSmallestAmountTxnDetailsResDTO getTransactionDetailsOfTheSmallestAmount(@PathVariable("cardId") Integer cardId) {
        logger.info("====== getTransactionDetailsOfTheSmallestAmount :: Input Received ====");
        logger.info("Card ID :: {}", cardId);
        GetSmallestAmountTxnDetailsResDTO txnDetail = null;
        if (cardId != null && cardId > 0) {
            txnDetail = txnService.getTransactionDetailsOfTheSmallestAmount(cardId);
        } else {
            txnDetail = new GetSmallestAmountTxnDetailsResDTO();
            txnDetail.setStatus("INVALID INPUT");
            txnDetail.setStatusCode("7000");
        }
        return txnDetail;
    }
}
