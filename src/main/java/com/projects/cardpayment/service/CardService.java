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
import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.entities.TxnDetails;
import com.projects.cardpayment.repository.CardRepository;
import com.projects.cardpayment.repository.TxnRepository;

@Service
public class CardService {

	Logger logger = LoggerFactory.getLogger(CardService.class);

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
		String txnUUID = null;
		Integer cardCurrentBalance = null;
		Integer cardNewBalance = null;
		try {
			card = cardRepository.findById(cardId).get();
			cardCurrentBalance = card.getCardBalance();
			cardNewBalance = cardCurrentBalance + amount;
			card.setCardBalance(cardNewBalance);
			card = cardRepository.save(card);
			logger.info("card {}", card);
			Date txnDate = new Date();
			txnUUID = txnPdf(null, card, amount);
			saveTransactionalDetails(null, null, card.getCardId(), card.getCardHolderFirstName(), amount, txnDate,
					"Amount Deposited", txnUUID);
		} catch (Exception e) {
			logger.info("CS : Exception  {}", e.getMessage());
		}
		return card;
	}

	public Card withdrawMoneyFromCard(Integer cardId, Integer amount) {
		Card card = null;
		Integer cardCurrentBalance = null;
		Integer cardNewBalance = null;
		String txnUUID = null;
		card = cardRepository.findById(cardId).get();
		cardCurrentBalance = card.getCardBalance();
		cardNewBalance = cardCurrentBalance - amount;
		card.setCardBalance(cardNewBalance);
		Date txnDate = new Date();
		try {
			txnUUID = txnPdf(card, null, amount);
			saveTransactionalDetails(card.getCardId(), card.getCardHolderFirstName(), null, null, amount, txnDate,
					"withdraw Transaction", txnUUID);
		} catch (FileNotFoundException | DocumentException e) {
			logger.info(e.getMessage());
		}
		return cardRepository.save(card);
	}

	public Map<String, String> orderPayment(Integer cardId, Integer cardCVV, String cardExpiryDate,
			Integer amountToBePaid) {
		Card card = null;
		String txnUUID = null;
		card = cardRepository.findById(cardId).get();
		Integer cardCurrentBalance = card.getCardBalance();
		if (cardCurrentBalance >= amountToBePaid) {
			logger.info("Card has sufficient amount for the txn");

			Integer cardCVVInDB = card.getCardCVVNumber();
			String cardExpiryDateInDB = card.getCardExpiryDate();
			logger.info("cardCVVInDB :: {} ", cardCVVInDB);
			logger.info("cardExpiryDateInDB :: {}", cardExpiryDateInDB);

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

				Date txnDate = new Date();

				try {
					txnUUID = txnPdf(card, null, amountToBePaid);
					logger.info("uuid generated : {}", txnUUID);
					saveTransactionalDetails(card.getCardId(), card.getCardHolderFirstName(), null, null,
							amountToBePaid, txnDate, "Shopping Transaction", txnUUID);
				} catch (FileNotFoundException | DocumentException e) {
					logger.info(e.getMessage());
				}

				logger.info("Txn Pdf creating Successfull");
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
			logger.info("Transactional details save successfully");

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

		logger.info("input received in money tranfer service");
		logger.info("sendercardId :: {},receiverCardId :: {},amount :: {}", senderCardId, receiverCardId, amount);

		Map<String, String> moneyTransferResponse = null;

		// Fetching receiver card details to credit amount in his card

		try {
			Integer serviceChargeAmount = 0;
			Integer txnAmount = amount;
			Card senderCard = cardRepository.findById(senderCardId).get();
			Card receiverCard = cardRepository.findById(receiverCardId).get();
			Integer senderCardBalance = senderCard.getCardBalance();
			if (!senderCard.getCardBankName().equals(receiverCard.getCardBankName())) {
				if (amount > 5000) {// 6000
					logger.info("amount is greater than 5000");
					logger.info("service charge will be deducted");
					Integer extraAmount = amount - 5000;
					serviceChargeAmount = (extraAmount * 5) / 100;
					logger.info("calculated service charged {}", serviceChargeAmount);

					amount = amount - serviceChargeAmount;// 5950
					logger.info("Final amount after service charge deduction :: {} ", amount);
				}
			}
			txnAmount = amount + serviceChargeAmount;// 5950+50=6000
			Integer senderUpdatedCardBalance = senderCardBalance - txnAmount;
			logger.info("newAmount{}", txnAmount);
			senderCard.setCardBalance(senderUpdatedCardBalance);
			cardRepository.save(senderCard);
			logger.info(" amount deducted from sender's card successfully {}", txnAmount);

			Integer receiverCardBalance = receiverCard.getCardBalance();
			Integer receiverUpdatedCardBalance = receiverCardBalance + amount;
			receiverCard.setCardBalance(receiverUpdatedCardBalance);
			cardRepository.save(receiverCard);
			logger.info(" amount credited to receiver's card successfully {}", amount);
			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put("status", "Success");
			moneyTransferResponse.put("message", "Amount transfer success !!");
			String txnUUID = txnPdf(senderCard, receiverCard, txnAmount);
			saveTransactionalDetails(senderCard.getCardId(), senderCard.getCardHolderFirstName(),
					receiverCard.getCardId(), receiverCard.getCardHolderFirstName(), amount, new Date(),
					"Card to card txn", txnUUID);
			logger.info("Txn Pdf creating Successfull");

		} catch (Exception e) {
			logger.info("EXCEPTION AT CS : {}", e.getMessage());
			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put("status", "FAILURE");
			moneyTransferResponse.put("message", "Amount transfer failed !!");
			moneyTransferResponse.put("reason", "Minimum txn amount must be 100");
		}
		return moneyTransferResponse;

	}

	private String txnPdf(Card senderCard, Card receiverCard, Integer txnAmount)
			throws FileNotFoundException, DocumentException {

		Document document = new Document(PageSize.LETTER);
		String uuid = String.valueOf(UUID.randomUUID());
		String pdfName = uuid + "_receipt.pdf";
		PdfWriter.getInstance(document, new FileOutputStream(pdfName));
		document.open();
		Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
		Paragraph para = new Paragraph("Money-Transfer Receipt", font);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		if (senderCard != null) {
			Paragraph para2 = new Paragraph("Sender CardID :: " + senderCard.getCardId(), font);
			document.add(para2);
		}
		if (receiverCard != null) {
			Paragraph para3 = new Paragraph("Receiver CardID :: " + receiverCard.getCardId(), font);
			document.add(para3);
		}
		Paragraph para4 = new Paragraph("Txn Amount :: " + "$" + txnAmount, font);
		document.add(para4);
		Paragraph para5 = new Paragraph("Txn ID :: " + uuid, font);
		document.add(para5);
		Paragraph para6 = new Paragraph("Txn Timestamp :: " + new Timestamp(System.currentTimeMillis()), font);
		document.add(para6);
		String username = System.getProperty("user.name") != null ? System.getProperty("user.name") : "default";
		Paragraph para7 = new Paragraph("Generated by system username :: " + username, font);
		document.add(para7);
		document.close();
		return uuid;
	}

	private void saveTransactionalDetails(Integer senderId, String senderName, Integer receiverId, String receiverName,
			Integer amount, Date txnDate, String purpose, String txnUUID) {

		TxnDetails txnDetails = new TxnDetails();

		txnDetails.setSenderName(senderName);
		txnDetails.setSenderId(senderId);
		txnDetails.setReceiverName(receiverName);
		txnDetails.setReceiverId(receiverId);
		txnDetails.setTxnAmount(amount);
		txnDetails.setTxnDate(txnDate);
		txnDetails.setPurpose(purpose);
		txnDetails.setTxnUUID(txnUUID);

		logger.info("saveTransactionalDetails {}", txnDetails);
		logger.info("setting txnUUID {}", txnUUID);
		txnRepository.save(txnDetails);
		logger.info("Transactional details save successfully");

	}

	public List<Card> getListOfExpiredCards() {
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
		Integer cardCVVNo = 0;
		Card card = cardRepository.findById(cardId).get();
		cardCVVNo = card.getCardCVVNumber();

		return cardCVVNo;

	}

	public List<Card> getListOfCardThatHaveBalanceInBetween(Integer lowerAmount, Integer upperAmount) {
		List<Card> allCards = null;
		List<Card> listOfCardBalance = new ArrayList<>();
		Card card = null;
		allCards = cardRepository.findAll();

		logger.info("allCards {}", allCards);

		for (int i = 0; i < allCards.size(); i++) {
			card = allCards.get(i);
			Integer cardBalance = card.getCardBalance();
			logger.info("card Balance*** {}", cardBalance);

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
		listOfCards = new ArrayList<>();
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
