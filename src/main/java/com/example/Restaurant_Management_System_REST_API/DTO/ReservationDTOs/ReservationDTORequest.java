package com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationRequest;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTORequest {

    private Long id;
    private String name;
    private String description;
    private int peopleAmount;
    private LocalDateTime start;
    private ArrayList<Table> tables;
    private CustomerDTOReservationRequest customer;
}
