package com.projects.cardpayment.controllers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.projects.cardpayment.daos.CardRepository;
import com.projects.cardpayment.entities.Card;

@RestController
@RequestMapping("/card-api")
public class CardController {

	Logger logger = LoggerFactory.getLogger(CardController.class);

	@Autowired
	private CardRepository cardRepository;

	@PostMapping("/create-card")
	public Map<String, String> createCard(@RequestBody Card card) {

		logger.info("====== creatCard :: Input Received ====");
		logger.info("Card to be created :: " + card);

		// Need to add validation for the card fields before saving it to DB
		// Basically not null checks

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
		logger.info("====== findACardById :: Input Received ====");
		logger.info("Card ID :: " + cardId);

		Card foundCard = cardRepository.findById(cardId).get();
		return foundCard;
	}

	@DeleteMapping("/delete-a-card-by-id/{cardId}")
	public String deleteACardById(@PathVariable("cardId") Integer cardId) {
		logger.info("====== deleteACardById :: Input Received ====");
		logger.info("Card ID :: " + cardId);

		cardRepository.deleteById(cardId);

		return "Card with id " + cardId + " deleted successfully";
	}

	@PatchMapping("/add-money-to-card")
	public Map<String, String> addMoneyToCard(@RequestParam("cardId") Integer cardId,
			@RequestParam("amount") Integer amount) {

		logger.info("===== addMoneyToCard :: Input Received ====");
		logger.info("Card ID :: " + cardId);
		logger.info("Amount to be added :: " + amount);

		if (amount > 0) {
			logger.info("Amount is greater than 0. Its a valid amount for the txn");

			Card card = cardRepository.findById(cardId).get();

			Integer cardCurrentBalance = card.getCardBalance();
			Integer cardNewBalance = cardCurrentBalance + amount;
			card.setCardBalance(cardNewBalance);
			cardRepository.save(card);

			logger.info(amount + " Amount added to the card");

			// now generating/preparing response for the API in case of SUCCESS
			Map<String, String> addMoneyResponseSuccess = new HashMap<>(3);
			addMoneyResponseSuccess.put("status", "SUCCESS");
			addMoneyResponseSuccess.put("amount", String.valueOf(amount));
			addMoneyResponseSuccess.put("message", "Money added successfully");
			return addMoneyResponseSuccess;
		}

		else {
			logger.info("Amount is less than 0. Its an invalid amount for the txn");

			// now generating/preparing response for the API in case of FAILURE
			Map<String, String> addMoneyResponseFailure = new HashMap<>(3);
			addMoneyResponseFailure.put("status", "FAILURE");
			addMoneyResponseFailure.put("amount", String.valueOf(amount));
			addMoneyResponseFailure.put("message", "Money could not be added successfully");
			return addMoneyResponseFailure;
		}

	}

	@PatchMapping("/withdraw-money-from-card")
	public Map<String, String> withdrawMoneyFromCard(@RequestParam("cardId") Integer cardId,
			@RequestParam("amount") Integer amount) {

		logger.info("====== withdrawMoneyFromCard :: Input Received ====");
		logger.info("Card ID :: " + cardId);
		logger.info("Withdrawal amount :: " + amount);

		if (amount > 0) {
			logger.info(amount + " amount is greater than 0. It is valid amount to proceed with the txn");

			Card card = cardRepository.findById(cardId).get();
			Integer cardCurrentBalance = card.getCardBalance();
			Integer cardNewBalance = cardCurrentBalance - amount;
			card.setCardBalance(cardNewBalance);

			cardRepository.save(card);

			// Preparing success response
			Map<String, String> withdrawMoneySuccessResponse = new HashMap<>();
			withdrawMoneySuccessResponse.put("status", "SUCCESS");
			withdrawMoneySuccessResponse.put("amount", String.valueOf(amount));
			withdrawMoneySuccessResponse.put("message", "Money withdrawn successfully");
			return withdrawMoneySuccessResponse;

		} else {
			logger.info(amount + " amount is 0 or less. It is invalid amount to proceed with the txn");

			// Preparing Failure response
			Map<String, String> withdrawMoneyFailureResponse = new HashMap<>();
			withdrawMoneyFailureResponse.put("status", "FAILURE");
			withdrawMoneyFailureResponse.put("amount", String.valueOf(amount));
			withdrawMoneyFailureResponse.put("message", "Money withdrawn failed");
			withdrawMoneyFailureResponse.put("reason", "Invalid amount");
			return withdrawMoneyFailureResponse;
		}

	}

	@PostMapping("/order-payment")
	public Map<String, String> orderPayment(@RequestParam("cardId") Integer cardId,
			@RequestParam("cardCVV") Integer cardCVV, @RequestParam("cardExpiryDate") String cardExpiryDate,
			@RequestParam("amount") Integer amountToBePaid) {

		logger.info("====== orderPayment :: Input Received ====");
		logger.info("Card ID :: " + cardId);
		logger.info("Card CVV :: " + cardCVV);
		logger.info("Card Expiry Date :: " + cardExpiryDate);
		logger.info("Amount to be paid :: " + amountToBePaid);

		// Need to do amount validation
		if (amountToBePaid > 0) {
			logger.info(amountToBePaid + " amount is greater than 0. It is a valid amount to proceed with the txn");

			Card card = cardRepository.findById(cardId).get();

			Integer cardCurrentBalance = card.getCardBalance();
			Integer cardCVVInDB = card.getCardCVVNumber();
			String cardExpiryDateInDB = card.getCardExpiryDate();

			// Need to do the Card validation before proceeding with the payment
			if (cardCVV == cardCVVInDB && cardExpiryDate == cardExpiryDateInDB) {

				logger.info("===Card CVV and Card Expiry Date is correct===");
				Integer cardBalance = card.getCardBalance();
				Integer updatedCardBalance = cardBalance - amountToBePaid;
				card.setCardBalance(updatedCardBalance);
				cardRepository.save(card);
				logger.info("Amount deducted and updated in db succesfully");

				// Preparing success response
				Map<String, String> orderPaymentSuccessResponse = new HashMap<>();
				orderPaymentSuccessResponse.put("status", "SUCCESS");
				return orderPaymentSuccessResponse;
			} else {
				logger.info("===Card CVV and/or Card Expiry Date are incorrect===");
				// Preparing failure response
				Map<String, String> orderPaymentFailureResponse = new HashMap<>();
				orderPaymentFailureResponse.put("status", "FAILURE");
				orderPaymentFailureResponse.put("cardCVV", String.valueOf(cardCVV));
				orderPaymentFailureResponse.put("cardExpiryDate", cardExpiryDate);
				orderPaymentFailureResponse.put("reason", "Invalid CARD CVV or EXPIRY DATE");
				return orderPaymentFailureResponse;
			}

		} else {
			logger.info(amountToBePaid + " amount is 0 or less. It is invalid amount to proceed with the txn");

			// Preparing failure response
			Map<String, String> orderPaymentFailureResponse = new HashMap<>();
			orderPaymentFailureResponse.put("status", "FAILURE");
			orderPaymentFailureResponse.put("reason", "Invalid amount value");
			orderPaymentFailureResponse.put("key1", "value1");
			return orderPaymentFailureResponse;
		}
	}

	@PostMapping("/money-transfer")
	public Map<String, String> moneyTransfer(@RequestParam("senderCardId") Integer senderCardId,
			@RequestParam("receiverCardId") Integer receiverCardId, @RequestParam("amount") Integer amount)
			throws DocumentException, FileNotFoundException {

		logger.info("===== moneyTransfer :: Input Received ====");
		logger.info("Sender CardID :: " + senderCardId);
		logger.info("Receiver CardID :: " + receiverCardId);
		logger.info("Amount to be transfer :: " + amount);

		if (amount >= 100) {
			logger.info(amount + " Amount is 100 or more . So its an valid amount to process with txn");

			// Fetching sender card details to deduct amount from his card
			Card senderCard = cardRepository.findById(senderCardId).get();
			Integer senderCardBalance = senderCard.getCardBalance();
			Integer senderUpdatedCardBalance = senderCardBalance - amount;
			senderCard.setCardBalance(senderUpdatedCardBalance);
			cardRepository.save(senderCard);
			logger.info(amount + " amount deducted from sender's card successfully");

			// Fetching receiver card details to credit amount in his card
			Card receiverCard = cardRepository.findById(receiverCardId).get();
			Integer receiverCardBalance = receiverCard.getCardBalance();
			Integer receiverUpdatedCardBalance = receiverCardBalance + amount;
			receiverCard.setCardBalance(receiverUpdatedCardBalance);
			cardRepository.save(receiverCard);
			logger.info(amount + " amount credited to receiver's card successfully");

			// Code to generate money transfer receipt pdf
			Document document = new Document(PageSize.A4);

			String uuid = String.valueOf(UUID.randomUUID());
			String pdfName = uuid + "_receipt.pdf";
			String folder = ".\\moneytransfer\\";

			PdfWriter.getInstance(document, new FileOutputStream(folder + pdfName));
			document.open();

			Font font = FontFactory.getFont(FontFactory.COURIER, 20, BaseColor.BLACK);
			Paragraph para = new Paragraph("Money-Transfer Receipt", font);
			para.setAlignment(Element.ALIGN_CENTER);
			document.add(para);

			Paragraph para2 = new Paragraph("Sender Card ID :: " + senderCardId, font);
			document.add(para2);

			Paragraph para3 = new Paragraph("Receiver Card ID :: " + receiverCardId, font);
			document.add(para3);

			Paragraph para4 = new Paragraph("Transaction Amount :: " + amount, font);
			document.add(para4);

			document.close();

			// Preparing response in case of success
			Map<String, String> moneyTransferSuccessResponse = new HashMap<>(5);
			moneyTransferSuccessResponse.put("senderCardId", String.valueOf(senderCardId));
			moneyTransferSuccessResponse.put("receiverCardId", String.valueOf(receiverCardId));
			moneyTransferSuccessResponse.put("amount", String.valueOf(amount));
			moneyTransferSuccessResponse.put("status", "SUCCESS");
			moneyTransferSuccessResponse.put("message", "Amount transfer is successfull");
			return moneyTransferSuccessResponse;

		} else {

			logger.info(amount + " Amount is less than 100. So its an invalid amount to process the txn");

			// Preparing response in case of success
			Map<String, String> moneyTransferFailureResponse = new HashMap<>(5);
			moneyTransferFailureResponse.put("senderCardId", String.valueOf(senderCardId));
			moneyTransferFailureResponse.put("receiverCardId", String.valueOf(receiverCardId));
			moneyTransferFailureResponse.put("amount", String.valueOf(amount));
			moneyTransferFailureResponse.put("status", "FAILURE");
			moneyTransferFailureResponse.put("message", "Amount transfer failed !!");
			moneyTransferFailureResponse.put("reason", "Invalid amount value");
			return moneyTransferFailureResponse;
		}

	}

	@GetMapping("/getListOfExpiredCard")
	public List<Card> getListOfExpiredCards() {

		List<Card> listOfExpiredCard = new ArrayList<Card>();
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
				logger.info("Card is expired" + card);
				listOfExpiredCard.add(card);
			}
		}

		return listOfExpiredCard;

	}

}
