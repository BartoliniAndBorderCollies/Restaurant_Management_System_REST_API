package com.example.Restaurant_Management_System_REST_API.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public abstract class CatalogItem {

    public CatalogItem(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @NotBlank(message = "Name is missing!")
    @Column(nullable = false)
    private String name;
    private String description;
    @PositiveOrZero(message = "Must be zero or more!")
    @NotNull(message = "Price is missing!")
    @Column(nullable = false)
    private Double price; //I use object to be able to hold null values (for updating process, I want to update
    //just fields which hold values, if some are skipped they should not be changed on db. if it was a primitive data type
    //of double then it would change to 0.0 if not provided other value in put request
}
