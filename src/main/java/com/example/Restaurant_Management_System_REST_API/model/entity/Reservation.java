package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotBlank(message = "Name is required!")
    private String name;
    private String description;
    @Digits(integer = 2, fraction = 0, message = "The number of people should be less than 100!")
    private int peopleAmount;
    @NotNull(message = "Start time of the reservation is required!")
    private LocalDateTime start;
    @ManyToMany(mappedBy = "reservationList")
    @JsonManagedReference
    private List<Table> tables;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
