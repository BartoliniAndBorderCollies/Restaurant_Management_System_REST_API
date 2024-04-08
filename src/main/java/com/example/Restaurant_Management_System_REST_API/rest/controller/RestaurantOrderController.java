package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.RestaurantOrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@AllArgsConstructor
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    @PostMapping("/add")
    public RestaurantOrderDTO add(@RequestBody RestaurantOrderDTO restaurantOrderDTO) throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException {
        return restaurantOrderService.create(restaurantOrderDTO);
    }

    @GetMapping("/find/{id}")
    public RestaurantOrderDTO findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return restaurantOrderService.findById(id);
    }

    @GetMapping("/findAll")
    public List<RestaurantOrderDTO> findAll() {
        return restaurantOrderService.findAll();
    }

    @PutMapping("/update/{id}")
    public RestaurantOrderDTO update(@PathVariable Long id, @RequestBody RestaurantOrderDTO restaurantOrderDTO)
            throws NotFoundInDatabaseException {
        return restaurantOrderService.update(id, restaurantOrderDTO);
    }
}
