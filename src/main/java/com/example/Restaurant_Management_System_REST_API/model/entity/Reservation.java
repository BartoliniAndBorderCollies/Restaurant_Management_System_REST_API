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
    @ManyToMany //now this is the owning side
    @JoinTable(name = "tables_and_reservations", joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id"))
//    @JsonManagedReference(value="reservation-table") this annotation would destroy tests, why? answers below in comments
    private List<Table> tables;
    @OneToOne
    @JoinColumn(name = "customer_id")
    @JsonManagedReference(value="customer-reservation")
    private Customer customer;

//    1. Test Expectations: Your tests might be expecting the full serialization of the Table entity when a Reservation is
//    serialized. When you uncomment the @JsonManagedReference annotation, the Table entity is omitted from the serialization,
//    causing the tests to fail.
//    2. Data Consistency: If your tests involve persisting and retrieving these entities, there might be inconsistencies
//    in the data due to the way the entities are mapped. This could lead to failures in the tests.
//    3. Lazy Loading: If your tests are set up in a way that they load the data lazily, and you’re trying to access the data
//    after the session is closed, it could lead to a LazyInitializationException.
}
