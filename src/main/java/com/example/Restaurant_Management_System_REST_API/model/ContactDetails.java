package com.example.Restaurant_Management_System_REST_API.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactDetails {
    @NotBlank(message = "Name must be provided.")
    private String name;
    @NotBlank(message = "Street must be provided.")
    private String street;
    @NotBlank(message = "House number must be provided.")
    private String houseNumber;
    @NotBlank(message = "City must be provided.")
    private String city;
    @NotBlank(message = "Postal code must be provided.")
    private String postalCode;
    @NotBlank(message = "Telephone number must be provided.")
    private String telephoneNumber;
}
