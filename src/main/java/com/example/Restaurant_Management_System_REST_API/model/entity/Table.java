package com.example.Restaurant_Management_System_REST_API.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private int chairsAmount;
    private boolean isAvailable;
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
    @OneToMany(mappedBy = "table")
    private List<Order> orders;
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
