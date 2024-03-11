package com.example.Restaurant_Management_System_REST_API.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactDetails {
    @NotBlank(message = "Name must be provided.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;

    @NotBlank(message = "Street must be provided.")
    private String street;

    @NotBlank(message = "House number must be provided.")
    private String houseNumber;

    @NotBlank(message = "City must be provided.")
    private String city;

    @NotBlank(message = "Postal code must be provided.")
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Postal code must be in the format 'XX-XXX'.")
    private String postalCode;

    @NotBlank(message = "Telephone number must be provided.")
    @Pattern(regexp = "\\d{9}", message = "Telephone number must be 9 characters.")
    private String telephoneNumber;
}
