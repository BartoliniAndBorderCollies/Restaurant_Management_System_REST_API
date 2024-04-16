package com.example.Restaurant_Management_System_REST_API.DTO.TableDTO;


import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationTableDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {

    private Long id;
    private List<ReservationTableDTO> reservationList = new ArrayList<>();
}
