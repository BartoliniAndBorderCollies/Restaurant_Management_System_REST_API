package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService implements GenericBasicCrudOperations<Reservation, Reservation, Long> {
    @Override
    public Reservation create(Reservation object) {
        return null;
    }

    @Override
    public Reservation read(Long aLong) {
        return null;
    }

    @Override
    public List<?> readAll() {
        return null;
    }

    @Override
    public Reservation update(Reservation object) {
        return null;
    }

    @Override
    public void delete(Reservation object) {

    }
}
