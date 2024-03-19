package com.projects.cardpayment.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.cardpayment.entities.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

}
