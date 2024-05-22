package com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;

import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderRequestDTO {

    private OrderStatus orderStatus;
    private TableReservationDTO table;
    private String telephoneNumber;
    private List<MenuRecordForOrderDTO> menuRecords;
}
