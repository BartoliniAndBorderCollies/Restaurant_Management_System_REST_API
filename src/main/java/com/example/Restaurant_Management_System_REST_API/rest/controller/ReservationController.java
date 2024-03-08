package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation/")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // this will be done by owner, manager and staff
    @PostMapping("/add")
    public ReservationDTOResponse create(@RequestBody ReservationDTORequest reservationDTORequest)
            throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException {
        return reservationService.create(reservationDTORequest);
    }

    // this will be done by owner, manager and staff
    @GetMapping("/find/{id}")
    public ReservationDTOResponse findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return reservationService.findById(id);
    }

    // this will be done by owner, manager and staff
    @GetMapping("/findAll")
    public List<ReservationDTOResponse> findAll() {
        return reservationService.findAll();
    }

    // this will be done by owner, manager and staff
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws NotFoundInDatabaseException {
        return reservationService.delete(id);
    }


}
