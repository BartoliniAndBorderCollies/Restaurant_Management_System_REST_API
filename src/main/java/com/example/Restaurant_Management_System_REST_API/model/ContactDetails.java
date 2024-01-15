package com.example.Restaurant_Management_System_REST_API.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class ContactDetails {

    private String name;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;
    private String telephoneNumber;
    private String emailAddress;
}
