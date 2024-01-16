package com.example.Restaurant_Management_System_REST_API.exception;

public class NotFoundInDatabaseException extends Exception{

    private static final String MESSAGE = "%s object has not been found";

    public NotFoundInDatabaseException(Class<?> clazz) {
        super(String.format(MESSAGE, clazz.getName()));
    }
}
