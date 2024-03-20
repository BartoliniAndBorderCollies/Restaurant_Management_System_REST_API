package com.example.Restaurant_Management_System_REST_API.DTO.TableDTOs;

import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableDTO {

    private Long id;
    private boolean isAvailable;
    private List<RestaurantOrder> restaurantOrders;
    private Reservation reservation;
}
