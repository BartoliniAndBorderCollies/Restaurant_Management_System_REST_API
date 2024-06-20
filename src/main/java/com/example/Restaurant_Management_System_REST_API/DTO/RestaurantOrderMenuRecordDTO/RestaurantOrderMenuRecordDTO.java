package com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderMenuRecordDTO;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderMenuRecordDTO {

    private List<MenuRecordForOrderDTO> menuRecord;
    private double totalAmountToPay;
}
