package com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RestaurantOrderDTO {

    private Long id;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    private TableReservationDTO table;
    private List<MenuRecordForOrderDTO> menuRecords;
}
