package com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDTO {

    private Long id;
    private String name;
    private String description;
    private int peopleAmount;
    private LocalDateTime start;
    private List<TableReservationDTO> tables;
    private CustomerReservationDTO customer;
}
