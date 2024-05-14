package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderMenuRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menuRecord_id")
    @JsonBackReference // it will be omitted from serialization. No risk of infinitive recursion.
    private MenuRecord menuRecord;

    @ManyToOne
    @JoinColumn(name = "restaurantOrder_id")
    @JsonBackReference // it will be omitted from serialization. No risk of infinitive recursion.
    private RestaurantOrder restaurantOrder;

    private Double portionsAmount;
}
