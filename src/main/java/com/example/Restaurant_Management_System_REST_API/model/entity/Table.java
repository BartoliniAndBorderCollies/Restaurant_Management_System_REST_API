package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "restaurant_tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private boolean isAvailable;
    @OneToMany(mappedBy = "table")
    private List<RestaurantOrder> restaurantOrders;
    @ManyToMany //this is the owning side because it has @JoinColumn
    @JoinTable(name = "tables_and_reservations", joinColumns = @JoinColumn(name = "table_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id"))
    @JsonBackReference
    private List<Reservation> reservationList;
}
