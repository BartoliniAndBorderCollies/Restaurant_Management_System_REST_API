package com.example.Restaurant_Management_System_REST_API.DTO.TableDTO;

import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableDTO {

    private Long id;
    private boolean isAvailable;
    private Reservation reservation;
}
