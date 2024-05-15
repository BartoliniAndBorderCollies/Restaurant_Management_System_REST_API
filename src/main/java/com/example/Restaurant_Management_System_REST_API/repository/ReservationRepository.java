package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    List<Reservation> findByName(String name);
}
