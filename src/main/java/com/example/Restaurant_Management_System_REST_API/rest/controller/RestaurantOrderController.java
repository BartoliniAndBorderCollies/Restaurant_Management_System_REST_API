package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.RestaurantOrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    @PostMapping("/add")
    public RestaurantOrderDTO add(@RequestBody RestaurantOrderDTO restaurantOrderDTO) throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException {
        return restaurantOrderService.create(restaurantOrderDTO);
    }
}
