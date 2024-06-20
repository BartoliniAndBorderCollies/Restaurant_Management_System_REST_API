package com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerReservationDTO;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;


@Getter
@Setter
public class ReservationForReportDTO {

    private Long id;
    private String name;
    private String description;
    private int peopleAmount;
    private LocalDateTime start;
    private CustomerReservationDTO customer;
}
