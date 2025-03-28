package com.example.phonebook.handler;

import com.example.phonebook.controller.response.ErrorResponseDto;
import com.example.phonebook.exception.CustomException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        String errorMessage = paramName + " parameter is required.";
        log.error("Missing parameter: {}", errorMessage);
        return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(Exception ex) {
        String defaultMessage = "Validation error";
        String errorMessage = defaultMessage;

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            errorMessage = methodArgumentNotValidException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst()
                    .orElse(defaultMessage);
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            errorMessage = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .findFirst()
                    .orElse(defaultMessage);
        }

        log.error("Validation error: {}", errorMessage);
        return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException ex) {
        log.error("Handling CustomException - Type: {}, Status: {}, Message: {}", ex.getClass().getSimpleName(), ex.getStatus(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        log.error("Internal Server Error: {}", ex.getMessage());
        return new ResponseEntity<>((new ErrorResponseDto("Internal Server Error: " + ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
