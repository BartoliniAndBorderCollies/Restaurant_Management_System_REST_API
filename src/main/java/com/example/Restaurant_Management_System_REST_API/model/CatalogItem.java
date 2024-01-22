package com.example.Restaurant_Management_System_REST_API.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class CatalogItem {

    private String name;
    private String description;
    private double price;
}
