package com.example.Restaurant_Management_System_REST_API.exception;

public class ObjectAlreadyExistException extends Exception{

    private static final String MESSAGE = "%s object already exist!";

    public ObjectAlreadyExistException(Class<?> clazz){
        super(String.format(MESSAGE, clazz.getName()));
    }
}
