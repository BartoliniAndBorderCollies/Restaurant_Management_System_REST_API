package com.example.Restaurant_Management_System_REST_API.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private int peopleAmount;
    private LocalDateTime start;
    @OneToMany(mappedBy = "reservation")
    private List<Table> tables;
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Customer customer;
}
