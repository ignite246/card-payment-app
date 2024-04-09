package com.projects.cardpayment.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.TxnDetails;
import com.projects.cardpayment.repository.CardRepository;
import com.projects.cardpayment.repository.TxnRepository;

@Service
public class Helper {

	private CardRepository cardRepository;
	private TxnRepository txnRepository;

	public Helper(CardRepository cardRepository, TxnRepository txnRepository) {
		this.cardRepository = cardRepository;
		this.txnRepository = txnRepository;
	}

	public List<Card> getAllCards() {
		return cardRepository.findAll();
	}

	public List<TxnDetails> getAllTxns() {
		return txnRepository.findAll();
	}

	public List<TxnDetails> getBySenderId(Integer cardId)
	{
	 return txnRepository.findBySenderId(cardId);	
	}
	
}
