package com.example.Restaurant_Management_System_REST_API.exception;

public class CustomerAlreadyHasReservationException extends Exception{

    private static final String MESSAGE = "This customer already has a reservation!";

    public CustomerAlreadyHasReservationException() {
        super(String.format(MESSAGE));
    }
}
