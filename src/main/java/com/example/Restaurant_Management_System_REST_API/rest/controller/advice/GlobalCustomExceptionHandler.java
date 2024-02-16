package com.example.Restaurant_Management_System_REST_API.rest.controller.advice;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalCustomExceptionHandler {

    @ExceptionHandler(NotFoundInDatabaseException.class)
    public ResponseEntity<?> handleNotFoundInDatabaseException (NotFoundInDatabaseException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<String> handlePropertyValueException (PropertyValueException ex) {
        return new ResponseEntity<>("Something is missing! You didn't specified a required field in your request: "
                + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
