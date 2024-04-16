package com.example.Restaurant_Management_System_REST_API.exception;

public class TableNotAvailableException extends RuntimeException{
    private static final String MESSAGE = "Table is not available at the requested time.";

    public TableNotAvailableException() {
        super(MESSAGE);
    }
}

