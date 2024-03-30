package com.projects.cardpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableTransactionManagement
@SpringBootApplication
public class CardPaymentAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardPaymentAppApplication.class, args);
	}

}
