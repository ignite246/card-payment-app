package com.projects.cardpayment.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.projects.cardpayment.controllers.CardController;
import com.projects.cardpayment.daos.CardRepository;
import com.projects.cardpayment.daos.TxnRepository;
import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.TxnDetails;

@Service
public class CardService {

	Logger logger = LoggerFactory.getLogger(CardController.class);

	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private TxnRepository txnRepository;

	public Card createCard(Card card) {

		logger.info("CPA : CS : Saving card details {}", card);
		Card savedCard = null;

		try {
			savedCard = cardRepository.save(card);
		} catch (Exception e) {
			logger.error("Exception found at CPA : CS ", e);
		}
		return savedCard;

	}

	public List<Card> getAllCards() {
		logger.info("inside getallCards service method");
		List<Card> allCards = cardRepository.findAll();

		logger.info("CS : number of cards fetched from db {}", allCards);
		return allCards;
	}

	public Card findCardById(Integer cardId) {
		Card card = null;
		card = cardRepository.findById(cardId).get();
		return card;
	}

	public void deleteById(Integer cardId) {

		cardRepository.deleteById(cardId);

	}

	public Card addMoneyToCard(Integer cardId, int amount) {
		Card card = null;
		Integer cardCurrentBalance = null;
		Integer cardNewBalance = null;

		try {
			card = cardRepository.findById(cardId).get();
			cardCurrentBalance = card.getCardBalance();
			cardNewBalance = cardCurrentBalance + amount;
			card.setCardBalance(cardNewBalance);
			card = cardRepository.save(card);
			logger.info("card {}", card);
		} catch (Exception e) {
			logger.info("CS : Exception  {}", e.getMessage());
		}
		return card;
	}

	public Card withdrawMoneyFromCard(Integer cardId, Integer amount) {
		// TODO Auto-generated method stub

		Card card = null;
		Integer cardCurrentBalance = null;
		Integer cardNewBalance = null;

		card = cardRepository.findById(cardId).get();
		cardCurrentBalance = card.getCardBalance();
		cardNewBalance = cardCurrentBalance - amount;
		card.setCardBalance(cardNewBalance);

		return cardRepository.save(card);

	}

	public Map<String, String> orderPayment(Integer cardId, Integer cardCVV, String cardExpiryDate,
			Integer amountToBePaid) {
		// TODO Auto-generated method stub
		Card card = null;
		card = cardRepository.findById(cardId).get();
		Integer cardCurrentBalance = card.getCardBalance();
		if (cardCurrentBalance >= amountToBePaid) {
			logger.info("Card has sufficient amount for the txn");

			Integer cardCVVInDB = card.getCardCVVNumber();
			String cardExpiryDateInDB = card.getCardExpiryDate();
			logger.info("cardCVVInDB :: " + cardCVVInDB);
			logger.info("cardExpiryDateInDB :: " + cardExpiryDateInDB);

			// Need to do the Card validation before proceeding with the payment
			if (Objects.equals(cardCVV, cardCVVInDB) && cardExpiryDate.equals(cardExpiryDateInDB)) {

				logger.info("===Card CVV and Card Expiry Date is correct===");
				Integer cardBalance = card.getCardBalance();
				Integer updatedCardBalance = cardBalance - amountToBePaid;
				card.setCardBalance(updatedCardBalance);
				cardRepository.save(card);
				logger.info("Amount deducted and updated in db successfully");

				// Preparing success response
				Map<String, String> orderPaymentSuccessResponse = new HashMap<>(3);
				orderPaymentSuccessResponse.put("status", "SUCCESS");
				orderPaymentSuccessResponse.put("amount", String.valueOf(amountToBePaid));
				orderPaymentSuccessResponse.put("txnId", String.valueOf(UUID.randomUUID()));
				return orderPaymentSuccessResponse;
			} else {
				logger.info("===Card CVV and/or Card Expiry Date are incorrect===");
				// Prsetteparing failure response
				Map<String, String> orderPaymentFailureResponse = new HashMap<>(2);
				orderPaymentFailureResponse.put("status", "FAILURE");
				orderPaymentFailureResponse.put("reason", "Invalid CARD CVV or EXPIRY DATE");
				return orderPaymentFailureResponse;
			}
		} else {
			logger.info("Card does not have sufficient amount fo txn");
			Map<String, String> orderPaymentFailureResponse = new HashMap<>();
			orderPaymentFailureResponse.put("status", "FAILURE");
			orderPaymentFailureResponse.put("reason", "Insufficient amount");
			orderPaymentFailureResponse.put("currentBalance", String.valueOf(cardCurrentBalance));
			return orderPaymentFailureResponse;
		}

//	} else {
//		logger.info(amountToBePaid + " amount is 0 or less. It is invalid amount to proceed with the txn");

		// Preparing failure response
//		Map<String, String> orderPaymentFailureResponse = new HashMap<>(2);
//		orderPaymentFailureResponse.put("status", "FAILURE");
//		orderPaymentFailureResponse.put("reason", "Invalid txn amount");
//		return orderPaymentFailureResponse;
	}

	public Map<String, String> moneyTransfer(Integer senderCardId, Integer receiverCardId, Integer amount)
			throws FileNotFoundException, DocumentException {
		// TODO Auto-generated method stub
		Map<String, String> moneyTransferResponse = null;

		// Fetching receiver card details to credit amount in his card

		try {
			Card senderCard = cardRepository.findById(senderCardId).get();
			Integer senderCardBalance = senderCard.getCardBalance();
			Integer senderUpdatedCardBalance = senderCardBalance - amount;
			senderCard.setCardBalance(senderUpdatedCardBalance);
			cardRepository.save(senderCard);
			logger.info(amount + " amount deducted from sender's card successfully");
			Card receiverCard = cardRepository.findById(receiverCardId).get();
			Integer receiverCardBalance = receiverCard.getCardBalance();
			Integer receiverUpdatedCardBalance = receiverCardBalance + amount;
			receiverCard.setCardBalance(receiverUpdatedCardBalance);
			cardRepository.save(receiverCard);
			logger.info(amount + " amount credited to receiver's card successfully");
			// Code to generate money transfer receipt pdf
			Document document = new Document(PageSize.LETTER);
			String uuid = String.valueOf(UUID.randomUUID());
			String pdfName = uuid + "_receipt.pdf";
			PdfWriter.getInstance(document, new FileOutputStream(pdfName));
			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
			Paragraph para = new Paragraph("Money-Transfer Receipt", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);
			Paragraph para2 = new Paragraph("Sender CardID :: " + senderCardId, font);
			document.add(para2);
			Paragraph para3 = new Paragraph("Receiver CardID :: " + receiverCardId, font);
			document.add(para3);
			Paragraph para4 = new Paragraph("Txn Amount :: " + "$" + amount, font);
			document.add(para4);
			Paragraph para5 = new Paragraph("Txn ID :: " + UUID.randomUUID(), font);
			document.add(para5);
			Paragraph para6 = new Paragraph("Txn Timestamp :: " + new Timestamp(System.currentTimeMillis()), font);
			document.add(para6);
			String username = System.getProperty("user.name") != null ? System.getProperty("user.name") : "default";
			Paragraph para7 = new Paragraph("Generated by system username :: " + username, font);
			document.add(para7);
			document.close();
			// Preparing response in case of success
			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put("senderCardId", String.valueOf(senderCardId));
			moneyTransferResponse.put("receiverCardId", String.valueOf(receiverCardId));
			moneyTransferResponse.put("amount", String.valueOf(amount));
			moneyTransferResponse.put("status", "SUCCESS");
			moneyTransferResponse.put("message", "Amount transferred successfully");
			  Date txnDate = new Date();
			saveTransactionalDetails(senderCard.getCardId(),senderCard.getCardHolderFirstName(),receiverCard.getCardId(),receiverCard.getCardHolderFirstName(),amount,txnDate);
			
			
		} catch (Exception e) {
			logger.info("EXCEPTION AT CS : {}", e.getMessage());
			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put("status", "FAILURE");
			moneyTransferResponse.put("message", "Amount transfer failed !!");
			moneyTransferResponse.put("reason", "Minimum txn amount must be 100");
		}
		return moneyTransferResponse;

	}
	
	

	private void saveTransactionalDetails(Integer senderId, String senderName, Integer receiverId,
			String receiverName, Integer amount, Date txnDate) {
//		card.setCardBalance(cardNewBalance);

		TxnDetails txnDetails=new TxnDetails();
		
		txnDetails.setSenderName(senderName);
		txnDetails.setSenderId(senderId);
		txnDetails.setReceiverName(receiverName);
		txnDetails.setReceiverId(receiverId);
		txnDetails.setTxnAmount(amount);
		txnDetails.setTxnDate(txnDate);
		
		txnRepository.save(txnDetails);
		logger.info("Transactional details save successfully");
		
		
	}

	public List<Card> getListOfExpiredCards() {
		// TODO Auto-generated method stub
		List<Card> listOfExpiredCard = new ArrayList<>();

		List<Card> cards = cardRepository.findAll();
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);

			String cardExpiryDate = card.getCardExpiryDate();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			// Parse the string into a LocalDate object
			LocalDate cardDate = LocalDate.parse(cardExpiryDate, formatter);

			// Compare with another date
			LocalDate currentDate = LocalDate.now();

			if (cardDate.isBefore(currentDate)) {
				logger.info("Card is expired {}", card);
				listOfExpiredCard.add(card);

			}
		}
		return listOfExpiredCard;
	}

	public Integer getCVVNumberByCardId(Integer cardId) {
		// TODO Auto-generated method stub
		Integer cardCVVNo = 0;
		Card card = cardRepository.findById(cardId).get();
		cardCVVNo = card.getCardCVVNumber();

		return cardCVVNo;

	}

	public List<Card> getListOfCardThatHaveBalanceInBetween(Integer lowerAmount, Integer upperAmount) {
		// TODO Auto-generated method stub
		List<Card> allCards = null;
		List<Card> listOfCardBalance = null;
		Card card = null;
		allCards = cardRepository.findAll();
		for (int i = 0; i < allCards.size(); i++) {
			card = allCards.get(i);
			Integer cardBalance = card.getCardBalance();

			if (cardBalance >= lowerAmount && cardBalance <= upperAmount) {

				listOfCardBalance.add(card);
			}
		}
		return listOfCardBalance;

	}

	public List<Card> getCardsByBankName(String bankName) {
		List<Card> allCards = null;
		List<Card> listOfCards = null;
		Card card = null;
		String cardBankName = null;

		allCards = cardRepository.findAll();
		listOfCards = new ArrayList<Card>();
		for (int i = 0; i < allCards.size(); i++) {
			card = allCards.get(i);
			cardBankName = card.getCardBankName();
			if (cardBankName.equalsIgnoreCase(bankName)) {
				listOfCards.add(card);
			}
		}
		return listOfCards;
	}

}
