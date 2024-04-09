package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestaurantOrderServiceTest {

    private ModelMapper modelMapper;
    private RestaurantOrderService restaurantOrderService;
    private RestaurantOrderRepository restaurantOrderRepository;
    private RestaurantOrderDTO restaurantOrderDTO;

    @BeforeEach
    public void setUpEnvironment() {
        modelMapper = mock(ModelMapper.class);
        restaurantOrderRepository = mock(RestaurantOrderRepository.class);
        restaurantOrderService = new RestaurantOrderService(restaurantOrderRepository, modelMapper);
        restaurantOrderDTO = mock(RestaurantOrderDTO.class);
    }


    @Test
    public void create_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenRestaurantOrderDTOIsGiven()
            throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException {
        //Arrange
        RestaurantOrder restaurantOrder = mock(RestaurantOrder.class);

        when(modelMapper.map(restaurantOrderDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);
        when(modelMapper.map(restaurantOrder, RestaurantOrderDTO.class)).thenReturn(restaurantOrderDTO);

        //Act
        RestaurantOrderDTO actual = restaurantOrderService.create(restaurantOrderDTO);

        //Assert
        assertEquals(restaurantOrderDTO, actual);
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.findById(nonExistedId));
    }

    @Test
    public void findById_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenRestaurantOrderIdIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Long id = 1L;
        RestaurantOrder restaurantOrder = mock(RestaurantOrder.class);
        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(modelMapper.map(restaurantOrder, RestaurantOrderDTO.class)).thenReturn(restaurantOrderDTO);

        //Act
        RestaurantOrderDTO actual = restaurantOrderService.findById(id);

        //Assert
        assertEquals(restaurantOrderDTO, actual);
    }

}