package com.projects.cardpayment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.cardpayment.entities.TxnDetails;

/**
 * TxnRepository to perform all card txn related DB operations,
 * like save, find, findAll, delete etc
 */

@Repository
public interface TxnRepository extends JpaRepository<TxnDetails,Integer>{
	

	List<TxnDetails> findBySenderId(Integer senderCardId);

}
