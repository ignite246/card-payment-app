package com.projects.cardpayment.controllers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.projects.cardpayment.entities.Card;
import com.projects.cardpayment.response.dto.CVVNumApiResponseDTO;
import com.projects.cardpayment.response.dto.CardsByBankResDTO;
import com.projects.cardpayment.response.dto.FindACardByIdResponseDTO;
import com.projects.cardpayment.response.dto.GetCardBalanceInBetweenResDTO;
import com.projects.cardpayment.service.CardService;
import com.projects.cardpayment.utils.Validation;

@RestController
@RequestMapping("/card-api")
public class CardController {

	Logger logger = LoggerFactory.getLogger(CardController.class);

	@Autowired
	private CardService cardService;

	@Autowired
	private Validation validation;

	@Value("${cardapp.c2cmoneytransfer.minimumTxnAmount}")
	private Integer minimumTxnAmount;

	@PostMapping("/create-card")
	public Map<String, String> createCard(@RequestBody Card card) {

		logger.info("====== createCard :: Input Received ====");
		logger.info("Card to be created :: {}", card);

		final boolean cardRequestValidation = validation.createCardRequestValidation(card);
		// cardRequestValidation :: true ==> the data is correct
		// cardRequestValidation :: false => the data is invalid/incorrect

		Map<String, String> createCardResponse = new HashMap<>(3);
		if (cardRequestValidation) {

			// Going to call service layer
			Card card2 = cardService.createCard(card);

			// got response from service layer
			createCardResponse.put("status", "SUCCESS");
			createCardResponse.put("cardId", String.valueOf(card2.getCardId()));
			createCardResponse.put("message", "Card created successfully");
		} else {
			createCardResponse.put("status", "FAILURE");
			createCardResponse.put("message", "Card creation failed");
			createCardResponse.put("reason", "Invalid card creation request body");
		}

		return createCardResponse;

	}

	@GetMapping("/get-all-cards")
	public List<Card> getAllCards() {
		logger.info("Inside getAllCards of CardController");
		List<Card> allCards = cardService.getAllCards();
		logger.info("CC : Response from sevice layer = {}", allCards);
		return allCards;
	}

	@GetMapping("/find-a-card-by-id/{cardId}")
	public FindACardByIdResponseDTO findACardById(@PathVariable("cardId") Integer cardId) {
		logger.info("findACardById :: Input	 Received");
		logger.info("Card ID :: {}", cardId);

		final FindACardByIdResponseDTO response = new FindACardByIdResponseDTO();

		try {

			Card foundCard = cardService.findCardById(cardId);
			response.setStatusMessage("SUCCESS");
			response.setStatusCode(7001);
			response.setCard(foundCard);

		} catch (Exception ex) {
			logger.error("Exception occurred while finding the card of the id :: {}", cardId);
			logger.error("Exception reason :: {} ", ex.getMessage());
			response.setStatusMessage("FAILURE");
			response.setStatusCode(8005); // Project specific status code
			response.setCard(null);
		}

		logger.info("findACardById API response ::{} ", response);
		return response;
	}

	@DeleteMapping("/delete-a-card-by-id/{cardId}")
	public Map<String, String> deleteACardById(@PathVariable("cardId") Integer cardId) {
		logger.info("====== deleteACardById :: Input Received ====");
		logger.info("Card ID ::{} ", cardId);

		final Map<String, String> deleteCardAPIResponse = new HashMap<>(4);
		try {

			cardService.deleteById(cardId);
			deleteCardAPIResponse.put("status", "SUCCESS");
			deleteCardAPIResponse.put("statusCode", "7000"); // Project specific status code //Not a standard HTTP
																// Status Code //Only project wale know ish code ka
																// meaning
			deleteCardAPIResponse.put("message", "Card deleted successfully");
			deleteCardAPIResponse.put("cardId", String.valueOf(cardId));
		} catch (Exception ex) {
			logger.error("Exception occurred while deleting the card of the id :: {} ", cardId);
			logger.error("Exception reason ::{} ", ex.getMessage());
			deleteCardAPIResponse.put("status", "FAILURE");
			deleteCardAPIResponse.put("statusCode", "8000"); // Project specific status code //Not a standard HTTP
																// Status Code //Only project wale know ish code ka
																// meaning
			deleteCardAPIResponse.put("message", "Card could not be deleted");
			deleteCardAPIResponse.put("cardId", String.valueOf(cardId));
			deleteCardAPIResponse.put("reason", "Card ID does not exist");
		}

		logger.info("deleteACardById API Response :: {} ", deleteCardAPIResponse);
		return deleteCardAPIResponse;
	}

	@PatchMapping("/add-money-to-card")
	public Map<String, String> addMoneyToCard(@RequestParam("cardId") Integer cardId,
			@RequestParam("amount") Integer amount) {

		Card card = null;
		logger.info("===== addMoneyToCard :: Input Received ====");
		logger.info("Card ID :: {}", cardId);
		logger.info("Amount to be added :: {}", amount);

		if (amount > 0) {
			logger.info("Amount is greater than 0. Its a valid amount for the txn");

			try {

				card = cardService.addMoneyToCard(cardId, amount);

				logger.info("{} Amount added to the card", amount);

				// Logic to store transaction details

				// now generating/preparing response for the API in case of SUCCESS
				Map<String, String> addMoneySuccessResponse = new HashMap<>(3);
				addMoneySuccessResponse.put("status", "SUCCESS");
				addMoneySuccessResponse.put("amount", String.valueOf(amount));
				addMoneySuccessResponse.put("message", "Money added successfully");
				return addMoneySuccessResponse;
			} catch (Exception ex) {
				Map<String, String> addMoneyFailureResponse = new HashMap<>(3);
				addMoneyFailureResponse.put("status", "FAILURE");
				addMoneyFailureResponse.put("amount", String.valueOf(amount));
				addMoneyFailureResponse.put("message", "Money could not be added successfully");
				addMoneyFailureResponse.put("reason", "Card Id does not exist");
				return addMoneyFailureResponse;
			}
		} else {
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
		logger.info("Card ID :: {} ", cardId);
		logger.info("Withdrawal amount :: {} ", amount);

		if (amount > 0) {
			logger.info("{} amount is greater than 0. It is valid amount to proceed with the txn", amount);
			cardService.withdrawMoneyFromCard(cardId, amount);

			// Preparing success response
			Map<String, String> withdrawMoneySuccessResponse = new HashMap<>();
			withdrawMoneySuccessResponse.put("status", "SUCCESS");
			withdrawMoneySuccessResponse.put("amount", String.valueOf(amount));
			withdrawMoneySuccessResponse.put("message", "Money withdrawn successfully");
			return withdrawMoneySuccessResponse;

		} else {
			logger.info("{} amount is 0 or less. It is invalid amount to proceed with the txn ", amount);
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
		Map<String, String> orderPayment = null;
		logger.info("====== orderPayment :: Input Received ====");
		logger.info("Card ID :: {}" , cardId);
		logger.info("Card CVV :: {}", cardCVV);
		logger.info("Card Expiry Date :: {}", cardExpiryDate);
		logger.info("Amount to be paid :: {}", amountToBePaid);

		// Need to do amount validation
		if (amountToBePaid > 0) {
			logger.info(" amount is greater than 0. It is a valid amount to proceed with the txn {}", amountToBePaid);
			orderPayment = new HashMap<>();
			orderPayment = cardService.orderPayment(cardId, cardCVV, cardExpiryDate, amountToBePaid);

		} else {
			logger.info("amount is 0 or less. It is invalid amount to proceed with the txn {}", amountToBePaid);
			orderPayment = new HashMap<>();
			orderPayment.put("status", "FAILURE");
			orderPayment.put("reason", "Invalid txn amount");
			return orderPayment;
		}
		return orderPayment;
	}

	@Transactional()
	@PostMapping("/money-transfer")
	public Map<String, String> moneyTransfer(@RequestParam("senderCardId") Integer senderCardId,
			@RequestParam("receiverCardId") Integer receiverCardId, @RequestParam("amount") Integer amount)
			throws DocumentException, FileNotFoundException {

		Map<String, String> moneyTransfer = null;
		logger.info("===== moneyTransfer :: Input Received ====");
		logger.info("Sender CardID :: {}", senderCardId);
		logger.info("Receiver CardID :: {}", receiverCardId);
		logger.info("Amount to be transfer :: {}", amount);

		if (amount >= minimumTxnAmount) {
			logger.info(" Amount is or more. So its an valid amount to process with txn {}{}", amount,
					minimumTxnAmount);
			// Fetching sender card details to deduct amount from his card
			moneyTransfer = cardService.moneyTransfer(senderCardId, receiverCardId, amount);
			return moneyTransfer;

		} else {
			logger.info("Amount is less than 100. So its an invalid amount to process the txn {}", amount);
			// Preparing response in case of success
			moneyTransfer = new HashMap<>(5);
			moneyTransfer.put("status", "FAILURE");
			moneyTransfer.put("message", "Amount transfer failed !!");
			moneyTransfer.put("reason", "Minimum txn amount must be 100");
		}
		return moneyTransfer;

	}

	@GetMapping("/getListOfExpiredCard")
	public List<Card> getListOfExpiredCards() {

		List<Card> listOfExpiredCard = new ArrayList<>();

		listOfExpiredCard = cardService.getListOfExpiredCards();

		return listOfExpiredCard;

	}

	@GetMapping("/getCVVNumberByCardId/{cardId}")
	public CVVNumApiResponseDTO getCVVNumberByCardId(@PathVariable("cardId") Integer cardId) {
		logger.info("input received in getCVVNumberByCardId :: {}", cardId);

		CVVNumApiResponseDTO cvvNumApiResponseDTO = new CVVNumApiResponseDTO();
		Integer cvvNumberByCardId = 0;
		try {

			cvvNumberByCardId = cardService.getCVVNumberByCardId(cardId);

			// Preparing success response
			cvvNumApiResponseDTO.setCardCVVNum(cvvNumberByCardId);
			cvvNumApiResponseDTO.setStatusCode(200); // OK
			cvvNumApiResponseDTO.setStatusMessage("Success");

		} catch (Exception e) {

			logger.error("exception occurred while fetching CVV number :: Exception Message :: {}", e.getMessage());

			cvvNumApiResponseDTO.setCardCVVNum(cvvNumberByCardId);
			cvvNumApiResponseDTO.setStatusCode(500); // Internal Server Code
			cvvNumApiResponseDTO.setStatusMessage("Failure");

			// Preparing failure response

		}

		logger.info("Response of the getCVVNumberByCardId API :: {} ", cvvNumApiResponseDTO);
		return cvvNumApiResponseDTO;
	}

	@GetMapping("/getListOfCardThatHaveBalanceInBetween")
	public GetCardBalanceInBetweenResDTO getListOfCardThatHaveBalanceInBetween(
			@RequestParam("lowerAmount") Integer lowerAmount, @RequestParam("upperAmount") Integer upperAmount) {
		GetCardBalanceInBetweenResDTO getCardBalanceInBetweenResDTO = null;
		List<Card> listOfCardBalance = null;
		try {
			if (lowerAmount > 0 && upperAmount > 0) {
				listOfCardBalance = new ArrayList<>();
				listOfCardBalance = cardService.getListOfCardThatHaveBalanceInBetween(lowerAmount, upperAmount);

			}
			getCardBalanceInBetweenResDTO = new GetCardBalanceInBetweenResDTO();
			if (listOfCardBalance == null || listOfCardBalance.isEmpty()) {
				getCardBalanceInBetweenResDTO.setStatus("Failure");
				getCardBalanceInBetweenResDTO.setStatusCode("1");// frontend team will have mapping as No Card Found
			} else {
				getCardBalanceInBetweenResDTO.setStatus("Success");
				getCardBalanceInBetweenResDTO.setStatusCode("0");
				getCardBalanceInBetweenResDTO.setCardList(listOfCardBalance);

			}
		} catch (Exception e) {
			logger.error("Exception found {}", e.getMessage());
		}
		return getCardBalanceInBetweenResDTO;
	}

	@GetMapping("/getCardsByBankName/{bankName}")
	public CardsByBankResDTO getCardsByBankName(@PathVariable String bankName) {
		CardsByBankResDTO cardsByBankResDTO = null;
		List<Card> cardsByBankName = null;
		try {

			if (bankName != null && 2 <= bankName.length()) {
				cardsByBankName = cardService.getCardsByBankName(bankName);

				cardsByBankResDTO = new CardsByBankResDTO();

				if (cardsByBankName == null || cardsByBankName.isEmpty()) {
					cardsByBankResDTO.setStatus("Failure");
					cardsByBankResDTO.setStatusCode("7000");// frontend team will have mapping as No Card Found
				} else {
					cardsByBankResDTO.setStatus("Success");
					cardsByBankResDTO.setStatusCode("8000");
					cardsByBankResDTO.setCardList(cardsByBankName);
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured while executing the getCardsByBankName {}", e.getMessage());

		}
		return cardsByBankResDTO;
	}

}
