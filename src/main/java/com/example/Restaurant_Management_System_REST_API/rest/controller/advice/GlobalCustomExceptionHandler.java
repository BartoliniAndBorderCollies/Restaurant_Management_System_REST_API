package com.example.Restaurant_Management_System_REST_API.rest.controller.advice;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalCustomExceptionHandler {

    @ExceptionHandler(NotFoundInDatabaseException.class)
    public ResponseEntity<?> handleNotFoundInDatabaseException (NotFoundInDatabaseException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
