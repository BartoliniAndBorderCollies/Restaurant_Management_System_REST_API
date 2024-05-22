package com.example.Restaurant_Management_System_REST_API.model.entity;

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
    private MenuRecord menuRecord;

    @ManyToOne
    @JoinColumn(name = "restaurantOrder_id")
    private RestaurantOrder restaurantOrder;

     private Double portionsAmount;
}
