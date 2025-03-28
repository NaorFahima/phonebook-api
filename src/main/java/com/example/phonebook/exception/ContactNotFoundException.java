package com.example.phonebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContactNotFoundException extends CustomException {
    public ContactNotFoundException(String id) {
        super("Contact id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}