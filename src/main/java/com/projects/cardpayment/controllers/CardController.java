package com.projects.cardpayment.controllers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.projects.cardpayment.dtos.FindACardByIdResponseDTO;
import com.projects.cardpayment.utils.Validation;
import jakarta.transaction.Transactional;
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
import com.projects.cardpayment.dtos.CVVNumApiResponseDTO;
import com.projects.cardpayment.dtos.CardsByBankResDTO;
import com.projects.cardpayment.dtos.GetCardBalanceInBetweenResDTO;
import com.projects.cardpayment.entities.Card;

@RestController
@RequestMapping("/card-api")
public class CardController {

    Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private Validation validation;

    @PostMapping("/create-card")
    public Map<String, String> createCard(@RequestBody Card card) {

        logger.info("====== createCard :: Input Received ====");
        logger.info("Card to be created :: " + card);

        // Need to add validation for the card fields before saving it to DB
        // Basically not null checks, minimum length checks, format of the expiryDate of the card

        final boolean cardRequestValidation = validation.createCardRequestValidation(card);

        Map<String, String> createCardResponse = new HashMap<>(3);
        if (cardRequestValidation) {
            Card savedCard = cardRepository.save(card);

            createCardResponse.put("status", "SUCCESS");
            createCardResponse.put("cardId", String.valueOf(savedCard.getCardId()));
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
        List<Card> allCards = cardRepository.findAll();
        return allCards;
    }

    @GetMapping("/find-a-card-by-id/{cardId}")
    public FindACardByIdResponseDTO findACardById(@PathVariable("cardId") Integer cardId) {
        logger.info("====== findACardById :: Input Received ====");
        logger.info("Card ID :: " + cardId);

        final FindACardByIdResponseDTO response = new FindACardByIdResponseDTO();

        try {
            Card foundCard = cardRepository.findById(cardId).get();
            response.setStatusMessage("SUCCESS");
            response.setStatusCode(7001);
            response.setCard(foundCard);

        } catch (Exception ex) {
            logger.error("Exception occurred while finding the card of the id :: " + cardId);
            logger.error("Exception reason :: " + ex.getMessage());
            response.setStatusMessage("FAILURE");
            response.setStatusCode(8005); //Project specific status code
            response.setCard(null);
        }

        logger.info("findACardById API response :: " + response);
        return response;
    }

    @DeleteMapping("/delete-a-card-by-id/{cardId}")
    public Map<String, String> deleteACardById(@PathVariable("cardId") Integer cardId) {
        logger.info("====== deleteACardById :: Input Received ====");
        logger.info("Card ID :: " + cardId);

        final Map<String, String> deleteCardAPIResponse = new HashMap<>(4);
        try {
            cardRepository.deleteById(cardId);
            deleteCardAPIResponse.put("status", "SUCCESS");
            deleteCardAPIResponse.put("statusCode", "7000"); //Project specific status code //Not a standard HTTP Status Code //Only project wale know ish code ka meaning
            deleteCardAPIResponse.put("message", "Card deleted successfully");
            deleteCardAPIResponse.put("cardId", String.valueOf(cardId));
        } catch (Exception ex) {
            logger.error("Exception occurred while deleting the card of the id :: " + cardId);
            logger.error("Exception reason :: " + ex.getMessage());
            deleteCardAPIResponse.put("status", "FAILURE");
            deleteCardAPIResponse.put("statusCode", "8000"); //Project specific status code //Not a standard HTTP Status Code //Only project wale know ish code ka meaning
            deleteCardAPIResponse.put("message", "Card could not be deleted");
            deleteCardAPIResponse.put("cardId", String.valueOf(cardId));
            deleteCardAPIResponse.put("reason", "Card ID does not exist");
        }

        logger.info("deleteACardById API Response :: " + deleteCardAPIResponse);
        return deleteCardAPIResponse;
    }

    @PatchMapping("/add-money-to-card")
    public Map<String, String> addMoneyToCard(@RequestParam("cardId") Integer cardId,
                                              @RequestParam("amount") Integer amount) {

        logger.info("===== addMoneyToCard :: Input Received ====");
        logger.info("Card ID :: " + cardId);
        logger.info("Amount to be added :: " + amount);

        if (amount > 0) {
            logger.info("Amount is greater than 0. Its a valid amount for the txn");

            try {
                Card card = cardRepository.findById(cardId).get();

                Integer cardCurrentBalance = card.getCardBalance();
                Integer cardNewBalance = cardCurrentBalance + amount;
                card.setCardBalance(cardNewBalance);
                cardRepository.save(card);

                logger.info(amount + " Amount added to the card");

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
                    // Preparing failure response
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

        } else {
            logger.info(amountToBePaid + " amount is 0 or less. It is invalid amount to proceed with the txn");

            // Preparing failure response
            Map<String, String> orderPaymentFailureResponse = new HashMap<>(2);
            orderPaymentFailureResponse.put("status", "FAILURE");
            orderPaymentFailureResponse.put("reason", "Invalid txn amount");
            return orderPaymentFailureResponse;
        }
    }

    @Transactional()
    @PostMapping("/money-transfer")
    public Map<String, String> moneyTransfer(@RequestParam("senderCardId") Integer senderCardId,
                                             @RequestParam("receiverCardId") Integer receiverCardId, @RequestParam("amount") Integer amount)
            throws DocumentException, FileNotFoundException {

        logger.info("===== moneyTransfer :: Input Received ====");
        logger.info("Sender CardID :: " + senderCardId);
        logger.info("Receiver CardID :: " + receiverCardId);
        logger.info("Amount to be transfer :: " + amount);

        if (amount >= 100) {
            logger.info(amount + " Amount is 100 or more. So its an valid amount to process with txn");

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
            Map<String, String> moneyTransferSuccessResponse = new HashMap<>(5);
            moneyTransferSuccessResponse.put("senderCardId", String.valueOf(senderCardId));
            moneyTransferSuccessResponse.put("receiverCardId", String.valueOf(receiverCardId));
            moneyTransferSuccessResponse.put("amount", String.valueOf(amount));
            moneyTransferSuccessResponse.put("status", "SUCCESS");
            moneyTransferSuccessResponse.put("message", "Amount transferred successfully");
            return moneyTransferSuccessResponse;

        } else {
            logger.info(amount + " Amount is less than 100. So its an invalid amount to process the txn");

            // Preparing response in case of success
            Map<String, String> moneyTransferFailureResponse = new HashMap<>(5);
            moneyTransferFailureResponse.put("status", "FAILURE");
            moneyTransferFailureResponse.put("message", "Amount transfer failed !!");
            moneyTransferFailureResponse.put("reason", "Minimum txn amount must be 100");
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


    @GetMapping("/getCVVNumberByCardId/{cardId}")
    public CVVNumApiResponseDTO getCVVNumberByCardId(@PathVariable("cardId") Integer cardId) {
        logger.info("input received in getCVVNumberByCardId :: " + cardId);

        Integer cardCVVNo = 0;
        CVVNumApiResponseDTO cvvNumApiResponseDTO = new CVVNumApiResponseDTO();

        try {

            Card card = cardRepository.findById(cardId).get();
            cardCVVNo = card.getCardCVVNumber();

            // Preparing success response
            cvvNumApiResponseDTO.setCardCVVNum(cardCVVNo);
            cvvNumApiResponseDTO.setStatusCode(200); // OK
            cvvNumApiResponseDTO.setStatusMessage("Success");

        } catch (Exception e) {

            logger.error("exception occurred while fetching CVV number :: Exception Message :: " + e.getMessage());

            cvvNumApiResponseDTO.setCardCVVNum(cardCVVNo);
            cvvNumApiResponseDTO.setStatusCode(500); // Internal Server Code
            cvvNumApiResponseDTO.setStatusMessage("Failure");

            // Preparing failure response

        }

        logger.info("Response of the getCVVNumberByCardId API :: " + cvvNumApiResponseDTO);
        return cvvNumApiResponseDTO;
    }

    @GetMapping("/getListOfCardThatHaveBalanceInBetween")
    public GetCardBalanceInBetweenResDTO getListOfCardThatHaveBalanceInBetween(
            @RequestParam("lowerAmount") Integer lowerAmount, @RequestParam("upperAmount") Integer upperAmount) {
        GetCardBalanceInBetweenResDTO getCardBalanceInBetweenResDTO = null;
        List<Card> listOfCardBalance = null;
        List<Card> allCards = null;
        Card card = null;
        try {
            if (lowerAmount > 0 && upperAmount > 0) {
                listOfCardBalance = new ArrayList<Card>();
                allCards = cardRepository.findAll();
                for (int i = 0; i < allCards.size(); i++) {
                    card = allCards.get(i);
                    Integer cardBalance = card.getCardBalance();

                    if (cardBalance >= lowerAmount && cardBalance <= upperAmount) {

                        listOfCardBalance.add(card);
                    }
                }
            }
            getCardBalanceInBetweenResDTO = new GetCardBalanceInBetweenResDTO();
            if (listOfCardBalance == null || listOfCardBalance.size() == 0) {
                getCardBalanceInBetweenResDTO.setStatus("Failure");
                getCardBalanceInBetweenResDTO.setStatusCode("1");// frontend team will have mapping as No Card Found
            } else {
                getCardBalanceInBetweenResDTO.setStatus("Success");
                getCardBalanceInBetweenResDTO.setStatusCode("0");
                getCardBalanceInBetweenResDTO.setCardList(listOfCardBalance);

            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
        return getCardBalanceInBetweenResDTO;
    }

    @GetMapping("/getCardsByBankName/{bankName}")
    public CardsByBankResDTO getCardsByBankName(@PathVariable String bankName) {
        CardsByBankResDTO cardsByBankResDTO = null;
        List<Card> allCards = null;
        List<Card> listOfCards = null;
        Card card = null;
        String cardBankName = null;
        try {

            if (bankName != null && 2 <= bankName.length()) {
                allCards = cardRepository.findAll();
                listOfCards = new ArrayList<Card>();
                for (int i = 0; i < allCards.size(); i++) {
                    card = allCards.get(i);
                    cardBankName = card.getCardBankName();
                    if (cardBankName.equalsIgnoreCase(bankName)) {
                        listOfCards.add(card);
                    }

                }
            }
            cardsByBankResDTO = new CardsByBankResDTO();

            if (listOfCards == null || listOfCards.size() == 0) {
                cardsByBankResDTO.setStatus("Failure");
                cardsByBankResDTO.setStatusCode("7000");// frontend team will have mapping as No Card Found
            } else {
                cardsByBankResDTO.setStatus("Success");
                cardsByBankResDTO.setStatusCode("8000");
                cardsByBankResDTO.setCardList(listOfCards);
            }
        } catch (Exception e) {
            System.out.print(e);

        }
        return cardsByBankResDTO;
    }


}
