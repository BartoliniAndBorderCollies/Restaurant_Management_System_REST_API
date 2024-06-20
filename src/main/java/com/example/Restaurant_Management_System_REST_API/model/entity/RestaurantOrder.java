package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    @ManyToOne
    @JoinColumn(name = "table_id")
    @JsonBackReference(value="table-restaurantOrder")
    private Table table;
    private String telephoneNumber;
    private double totalAmountToPay;
    @OneToMany(mappedBy = "restaurantOrder")
    private List<RestaurantOrderMenuRecord> restaurantOrders;
}