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

import com.projects.cardpayment.constant.CardPaymentConstants;
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
import com.projects.cardpayment.entities.User;
import com.projects.cardpayment.repository.CardRepository;
import com.projects.cardpayment.repository.TxnRepository;
import com.projects.cardpayment.utils.mailservice.MailService;
import com.projects.cardpayment.utils.mailservice.MailStructure;

@Service
public class CardService {

	Logger logger = LoggerFactory.getLogger(CardService.class);

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private TxnRepository txnRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private UserAppService userAppService;

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

	public boolean deleteById(Integer cardId, String userName, String password) {
		logger.info("VALIDATING USERNAME AND PASSWORD userName:{},psssword:{}", userName, password);
		User user = userAppService.userLoginService(userName, password);
		if (user != null) {
			logger.info("LOGIN VALIDATION SUCCESSFULL");
			String role = user.getRole();
			if (role.equalsIgnoreCase("ADMIN")) {
				logger.info("ADMIN VALIDATION SUCCESSFULL");
				cardRepository.deleteById(cardId);
				return true;
			} else {
				logger.info("ADMIN VALIDATION FAILED");
				return false;
			}
		} else {
			logger.info("user doesn't exist");
			return false;
		}
	}

	public String addMoneyToCard(Integer cardId, int amount) {
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
			txnUUID = generateTxnReceiptPdf(null, card, amount);
			saveTransactionalDetails(card.getCardId(), card.getCardHolderFirstName(), card.getCardId(),
					card.getCardHolderFirstName(), amount, txnDate, CardPaymentConstants.SELF_DEPOSIT, txnUUID);

			// we will also send a mail to this card holder
			MailStructure mailStructure = new MailStructure();
			mailStructure.setSubject("Add Money To Card");
			mailStructure.setMessage(amount + " has been added to your card successfully with txnId :: " + txnUUID);

			mailService.sendEmail(mailStructure, card.getEmail());
			logger.info("Mail has been send to {} Successfully.", card.getEmail());

		} catch (Exception e) {
			logger.info("CS : Exception  {}", e.getMessage());
		}
		return txnUUID;
	}

	public String withdrawMoneyFromCard(Integer cardId, Integer amount) {
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
			txnUUID = generateTxnReceiptPdf(card, null, amount);
			saveTransactionalDetails(card.getCardId(), card.getCardHolderFirstName(), card.getCardId(),
					card.getCardHolderFirstName(), amount, txnDate, CardPaymentConstants.SELF_WITHDRAWAL, txnUUID);
			cardRepository.save(card);

			MailStructure mailStructure = new MailStructure();
			mailStructure.setSubject("WithDrawal money from Card");
			mailStructure
					.setMessage(amount + " has been withdrawal to your card successfully with txnId :: " + txnUUID);

			mailService.sendEmail(mailStructure, card.getEmail());
			logger.info("Mail has been send to {} Successfully.", card.getEmail());
		} catch (FileNotFoundException | DocumentException e) {
			logger.error(e.getMessage());
		}
		return txnUUID;
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
				try {
					logger.info("===Card CVV and Card Expiry Date are correct===");
					Integer cardBalance = card.getCardBalance();
					Integer updatedCardBalance = cardBalance - amountToBePaid;
					card.setCardBalance(updatedCardBalance);
					cardRepository.save(card);
					logger.info("Amount deducted and updated in db successfully");

					// Preparing success response

					Date txnDate = new Date();
					txnUUID = generateTxnReceiptPdf(card, null, amountToBePaid);
					logger.info("Txn UUID generated : {}", txnUUID);
					logger.info("Txn Receipt Pdf created Successfully");

					saveTransactionalDetails(card.getCardId(), card.getCardHolderFirstName(), 0, "ECOMMERCE_APP",
							amountToBePaid, txnDate, CardPaymentConstants.ORDER_PAYMENT, txnUUID);
					logger.info("Transaction details saved successfully");

					MailStructure mailStructure = new MailStructure();
					mailStructure.setSubject("Order Payment from Card");
					mailStructure.setMessage("Dear " + card.getCardHolderFirstName() + ",\n" + amountToBePaid
							+ " has been deducted(order payment) from your card successfully.\n" + "Txn ID : " + txnUUID
							+ "\n\n" + "Thanks & Regards, \n" + "CardPaymentApp");

					mailService.sendEmail(mailStructure, card.getEmail());
					logger.info("Mail has been sent to {} successfully.", card.getEmail());

				} catch (FileNotFoundException | DocumentException e) {
					logger.error("Exception occurred in orderPayment service {}", e.getMessage());
				} catch (Exception ex) {
					logger.error("Main exception occurred");
				}

				Map<String, String> orderPaymentSuccessResponse = new HashMap<>(3);
				orderPaymentSuccessResponse.put(CardPaymentConstants.STATUS, CardPaymentConstants.SUCCESS);
				orderPaymentSuccessResponse.put("amount", String.valueOf(amountToBePaid));
				orderPaymentSuccessResponse.put("txnId", txnUUID);

				return orderPaymentSuccessResponse;

			} else {
				logger.info("===Card CVV and/or Card Expiry Date are incorrect===");
				Map<String, String> orderPaymentFailureResponse = new HashMap<>(2);
				orderPaymentFailureResponse.put(CardPaymentConstants.STATUS, CardPaymentConstants.FAILURE);
				orderPaymentFailureResponse.put(CardPaymentConstants.REASON, "Invalid CARD CVV or EXPIRY DATE");
				return orderPaymentFailureResponse;
			}
		} else {
			logger.info("Card does not have sufficient amount fo txn");
			Map<String, String> orderPaymentFailureResponse = new HashMap<>();
			orderPaymentFailureResponse.put(CardPaymentConstants.STATUS, CardPaymentConstants.FAILURE);
			orderPaymentFailureResponse.put(CardPaymentConstants.REASON, "Insufficient amount");
			orderPaymentFailureResponse.put("currentBalance", String.valueOf(cardCurrentBalance));

			return orderPaymentFailureResponse;
		}
	}

	public Map<String, String> moneyTransfer(Integer senderCardId, Integer receiverCardId, Integer amount)
			throws FileNotFoundException, DocumentException {
		logger.info("Input received in money tranfer service");
		logger.info("senderCardId :: {}, receiverCardId :: {}, amount :: {}", senderCardId, receiverCardId, amount);
		Map<String, String> moneyTransferResponse = null;
		try {
			Integer serviceChargeAmount = 0;
			Integer txnAmount = amount;
			Card senderCard = cardRepository.findById(senderCardId).get();
			Card receiverCard = cardRepository.findById(receiverCardId).get();
			Integer senderCardBalance = senderCard.getCardBalance();

			if (!senderCard.getCardBankName().equals(receiverCard.getCardBankName())) {
				if (amount > 5000) {
					logger.info("amount is greater than 5000");
					logger.info("service charge will be deducted");
					Integer extraAmount = amount - 5000;
					serviceChargeAmount = (extraAmount * 5) / 100;
					logger.info("calculated service charged {}", serviceChargeAmount);
					amount = amount - serviceChargeAmount;
					logger.info("Final amount after service charge deduction :: {} ", amount);
				}
			}
			txnAmount = amount + serviceChargeAmount;
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

			String txnUUID = generateTxnReceiptPdf(senderCard, receiverCard, txnAmount);
			saveTransactionalDetails(senderCard.getCardId(), senderCard.getCardHolderFirstName(),
					receiverCard.getCardId(), receiverCard.getCardHolderFirstName(), amount, new Date(),
					CardPaymentConstants.CARD_2_CARD_MONEY_TRANSFER, txnUUID);
			logger.info("Txn Pdf created Successfully");
			logger.info(" amount deducted from sender's card successfully {}", txnAmount);

			MailStructure mailStructure = new MailStructure();
			mailStructure.setSubject("txnAmount from Card");
			mailStructure
					.setMessage(txnAmount + " has been debited from your card successfully with txnId :: " + txnUUID);

			mailService.sendEmail(mailStructure, senderCard.getEmail());
			logger.info("Mail has been send to {} Successfully.", senderCard.getEmail());

			mailStructure.setSubject("txnAmount to Card");
			mailStructure.setMessage(amount + " has been credited to your card successfully with txnId :: " + txnUUID);

			mailService.sendEmail(mailStructure, receiverCard.getEmail());
			logger.info("Mail has been send to {} Successfully.", receiverCard.getEmail());

			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put(CardPaymentConstants.STATUS, CardPaymentConstants.SUCCESS);
			moneyTransferResponse.put(CardPaymentConstants.STATUS_MSG, "Amount transfer success !!");
			moneyTransferResponse.put("amount", String.valueOf(txnAmount));
			moneyTransferResponse.put("txnUUID", txnUUID);

		} catch (Exception e) {
			logger.info("EXCEPTION AT CS : {}", e.getMessage());
			moneyTransferResponse = new HashMap<>(5);
			moneyTransferResponse.put(CardPaymentConstants.STATUS, CardPaymentConstants.FAILURE);
			moneyTransferResponse.put(CardPaymentConstants.STATUS_MSG, "Amount transfer failed !!");
			moneyTransferResponse.put("reason", "Minimum txn amount must be 100");
		}
		return moneyTransferResponse;
	}

	private String generateTxnReceiptPdf(Card senderCard, Card receiverCard, Integer txnAmount)
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
