package com.projects.cardpayment.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.cardpayment.entities.TxnDetails;

@Repository
public interface TxnRepository extends JpaRepository<TxnDetails,Integer>{
	


}
