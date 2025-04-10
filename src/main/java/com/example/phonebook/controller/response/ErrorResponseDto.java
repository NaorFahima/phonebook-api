package com.example.phonebook.controller.response;

public class ErrorResponseDto {
    private String message;
    public ErrorResponseDto(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorResponseDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
