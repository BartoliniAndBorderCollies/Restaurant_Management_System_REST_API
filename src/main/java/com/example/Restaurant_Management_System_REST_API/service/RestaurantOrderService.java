package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantOrderService implements GenericBasicCrudOperations<RestaurantOrderDTO, RestaurantOrderDTO, Long> {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ModelMapper modelMapper;

    @Override
    public RestaurantOrderDTO create(RestaurantOrderDTO restaurantOrderDTO) throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException {
        RestaurantOrder restaurantOrder = modelMapper.map(restaurantOrderDTO, RestaurantOrder.class);
        restaurantOrderRepository.save(restaurantOrder);

        return modelMapper.map(restaurantOrder, RestaurantOrderDTO.class);
    }

    @Override
    public RestaurantOrderDTO findById(Long id) throws NotFoundInDatabaseException {
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(() -> new
                NotFoundInDatabaseException(RestaurantOrder.class));

        return modelMapper.map(restaurantOrder, RestaurantOrderDTO.class);
    }

    @Override
    public List<RestaurantOrderDTO> findAll() {
        List<RestaurantOrderDTO> orderDTOList = new ArrayList<>();
        restaurantOrderRepository.findAll().forEach(restaurantOrder -> {
            RestaurantOrderDTO orderDto = modelMapper.map(restaurantOrder, RestaurantOrderDTO.class);
            orderDTOList.add(orderDto);

        });
        return orderDTOList;
    }

    @Override
    public RestaurantOrderDTO update(Long id, RestaurantOrderDTO updatedOrderDTO) throws NotFoundInDatabaseException {

        RestaurantOrder existingRestaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(RestaurantOrder.class));
        RestaurantOrder restaurantOrder = modelMapper.map(updatedOrderDTO, RestaurantOrder.class);

        Optional.ofNullable(restaurantOrder.getOrderStatus()).ifPresent(existingRestaurantOrder::setOrderStatus);
        Optional.ofNullable(restaurantOrder.getTable()).ifPresent(table -> {
            //TODO: when branch add_table_layers will be merged do the following steps:
            //TODO: check if table exist
            //TODO: if exists take this object and its id and set this object to existingRestaurantOrder and save
            existingRestaurantOrder.setTable(table);
            restaurantOrderRepository.save(existingRestaurantOrder);
        });

        Optional.ofNullable(restaurantOrder.getMenuRecords()).ifPresent(existingRestaurantOrder::setMenuRecords);
        //TODO: finish this logic in the next branch

        restaurantOrderRepository.save(existingRestaurantOrder);

        return modelMapper.map(existingRestaurantOrder, RestaurantOrderDTO.class);
    }

    @Override
    public ResponseEntity<?> delete(Long aLong) throws NotFoundInDatabaseException {
        return null;
    }
}
