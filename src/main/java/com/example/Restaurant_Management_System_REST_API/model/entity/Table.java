package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Table(name = "restaurant_tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private boolean isAvailable;
    @OneToMany(mappedBy = "table")
    private List<RestaurantOrder> restaurantOrders;
    @ManyToMany(mappedBy = "tables")
    @JsonBackReference(value="reservation-table")
    private List<Reservation> reservationList;
}
