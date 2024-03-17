package com.projects.cardpayment.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.cardpayment.dtos.CardRepository;
import com.projects.cardpayment.entities.Card;

@RestController
@RequestMapping("/card-api")
public class CardController {

	@Autowired
	private CardRepository cardRepository;

	@PostMapping("/create-card")
	public Map<String, String> createCard(@RequestBody Card card) {
		Card savedCard = cardRepository.save(card);

		Map<String, String> createCardResponse = new HashMap<>(2);
		createCardResponse.put("cardId", String.valueOf(savedCard.getCardId()));
		createCardResponse.put("message", "Card created successfully");

		return createCardResponse;

	}

	@GetMapping("/get-all-cards")
	public List<Card> getAllCards() {
		List<Card> allCards = cardRepository.findAll();
		return allCards;
	}

	@GetMapping("/find-a-card-by-id/{cardId}")
	public Card findACardById(@PathVariable("cardId") Integer cardId) {
		Card foundCard = cardRepository.findById(cardId).get();
		return foundCard;
	}

	@DeleteMapping("/delete-a-card-by-id/{cardId}")
	public String deleteACardById(@PathVariable("cardId") Integer cardId) {
		cardRepository.deleteById(cardId);
		return "Card with id " + cardId + " deleted successfully";
	}
	
	
	@PatchMapping("/add-money-to-card")
	public Map<String, String> addMoneyToCard(@RequestParam("cardId") Integer cardId, @RequestParam("amount") Integer amount){
		Card card = cardRepository.findById(cardId).get();
		Integer cardCurrentBalance = card.getCardBalance();
		Integer cardNewBalance = cardCurrentBalance + amount;
		card.setCardBalance(cardNewBalance);
		
		cardRepository.save(card);
		
		Map<String, String> addMoneyResponse = new HashMap<>();
		addMoneyResponse.put("status", "SUCCESS");
		addMoneyResponse.put("amount", String.valueOf(amount));
		addMoneyResponse.put("message", "Money added successfully");
		
		return addMoneyResponse;
	
	}
	
	
	@PatchMapping("/withdraw-money-from-card")
	public Map<String, String> withdrawMoneyFromCard(@RequestParam("cardId") Integer cardId, @RequestParam("amount") Integer amount){
		Card card = cardRepository.findById(cardId).get();
		Integer cardCurrentBalance = card.getCardBalance();
		Integer cardNewBalance = cardCurrentBalance - amount;
		card.setCardBalance(cardNewBalance);
		
		cardRepository.save(card);
		
		Map<String, String> withdrawMoneyResponse = new HashMap<>();
		withdrawMoneyResponse.put("status", "SUCCESS");
		withdrawMoneyResponse.put("amount", String.valueOf(amount));
		withdrawMoneyResponse.put("message", "Money withdrwan successfully");
		
		return withdrawMoneyResponse;
	
	}
	
	
	@PostMapping("/order-payment")
	public Map<String, String> orderPayment(
			@RequestParam("cardId") Integer cardId, 
			@RequestParam("cardCVV") Integer cardCVV, 
			@RequestParam("cardExpiryDate") String cardExpiryDate ){
		
		//Need to do amount validation
		
		
		Card card = cardRepository.findById(cardId).get();
		
		Integer cardCurrentBalance = card.getCardBalance();
		Integer cardCVVInDB = card.getCardCVVNumber();
		String cardExpiryDateInDB = card.getCardExpiryDate();
		
		//Need to do the Card validation before payment
		
		
		
		Map<String, String> orderPaymentResponse = new HashMap<>();
		return orderPaymentResponse;
	}
	
	
	// I made a mistake ==> I deleted the whole project by mistake ***
	// I made a mistake ==> I deleted the whole project by mistake ***
	
	
	
	
	
	
	
	
	
	

}
