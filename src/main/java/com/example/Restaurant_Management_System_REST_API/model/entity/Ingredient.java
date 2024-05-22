package com.example.Restaurant_Management_System_REST_API.model.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Data
public class Ingredient {

    private String name;
    private double amountRequired;
}
