package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderRequestDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotEnoughIngredientsException;
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
    public RestaurantOrderResponseDTO add(@RequestBody RestaurantOrderRequestDTO restaurantOrderResponseDTO)
            throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException, NotEnoughIngredientsException {
        return restaurantOrderService.create(restaurantOrderResponseDTO);
    }

    @GetMapping("/find/{id}")
    public RestaurantOrderResponseDTO findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return restaurantOrderService.findById(id);
    }

    @GetMapping("/findAll")
    public List<RestaurantOrderResponseDTO> findAll() {
        return restaurantOrderService.findAll();
    }

    @PutMapping("/update/{id}")
    public RestaurantOrderResponseDTO update(@PathVariable Long id, @RequestBody RestaurantOrderRequestDTO restaurantOrderResponseDTO)
            throws NotFoundInDatabaseException {
        return restaurantOrderService.update(id, restaurantOrderResponseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO delete(@PathVariable Long id) throws NotFoundInDatabaseException {
        return restaurantOrderService.delete(id);
    }
}
