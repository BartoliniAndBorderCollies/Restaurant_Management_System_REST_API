package com.example.Restaurant_Management_System_REST_API.DTO.TableDTO;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationForReportDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableForReportDTO {

    private Long id;
    private boolean isAvailable;
    private List<ReservationForReportDTO> reservationList;
}
