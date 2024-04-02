package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTO;
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
    public ReservationDTO create(@RequestBody ReservationDTO reservationDTO)
            throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException {
        return reservationService.create(reservationDTO);
    }

    // this will be done by owner, manager and staff
    @GetMapping("/find/{id}")
    public ReservationDTO findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return reservationService.findById(id);
    }

    // this will be done by owner, manager and staff
    @GetMapping("/findAll")
    public List<ReservationDTO> findAll() {
        return reservationService.findAll();
    }

    // this will be done by owner, manager and staff
    @PutMapping("/update/{id}")
    public ReservationDTO update(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO)
            throws NotFoundInDatabaseException {
        return reservationService.update(id, reservationDTO);
    }

    // this will be done by owner, manager and staff
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws NotFoundInDatabaseException {
        return reservationService.delete(id);
    }
}
