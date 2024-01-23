package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class MenuRecord extends CatalogItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotEmpty(message = "Must have at least one ingredient!")
    @Column(nullable = false)
    private Set<String> ingredients;
    @NotNull (message = "Category is missing!")
    @Column(nullable = false)
    private Category category;
    @NotNull(message = "Is available is missing!")
    @Column(nullable = false)
    private Boolean isAvailable; //I use object to be able to hold null values (for updating process, I want to update
    //just fields which hold values, if some are skipped they should not be changed on db. if it was a primitive data type
    //of boolean then it would change to false as default
}
