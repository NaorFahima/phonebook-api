package com.example.phonebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.phonebook")
public class PhoneBookApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhoneBookApiApplication.class, args);
	}

}
