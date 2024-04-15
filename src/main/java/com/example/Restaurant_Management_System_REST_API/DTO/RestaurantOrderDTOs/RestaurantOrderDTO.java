package com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantOrderDTO {

    private Long id;
    private LocalDateTime orderTime;
    private OrderStatus orderStatus;
    private TableDTO table;
    private List<MenuRecordDTOResponse> menuRecords;
}