package com.projects.cardpayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.cardpayment.entities.Card;

/**
 * CardRepository to perform all card related db operations,
 * like save, find, findall, delete etc
 */

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

}
