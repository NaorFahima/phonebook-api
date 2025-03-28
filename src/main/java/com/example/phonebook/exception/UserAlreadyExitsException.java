package com.example.phonebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExitsException extends CustomException {
    public UserAlreadyExitsException(String phoneNumber) {
        super("User with phone number " + phoneNumber + " already exist", HttpStatus.CONFLICT);
    }
}