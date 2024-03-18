package com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDTOResponse {

    private Long id;
    private String name;
    private String description;
    private int peopleAmount;
    private LocalDateTime start;
    private List<Table> tables;
    private CustomerDTOReservationResponse customer;
}
