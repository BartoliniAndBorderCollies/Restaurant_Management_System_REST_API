package com.example.Restaurant_Management_System_REST_API.exception;

public class NotEnoughIngredientsException extends Exception{

    private static final String MESSAGE = "Not enough ingredients to make this order!";

    public NotEnoughIngredientsException() {
        super(String.format(MESSAGE));
    }
}
