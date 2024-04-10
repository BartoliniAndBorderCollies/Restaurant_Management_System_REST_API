package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestaurantOrderServiceTest {

    private ModelMapper modelMapper;
    private RestaurantOrderService restaurantOrderService;
    private RestaurantOrderRepository restaurantOrderRepository;
    private RestaurantOrderDTO restaurantOrderDTO;
    private RestaurantOrder restaurantOrder;

    @BeforeEach
    public void setUpEnvironment() {
        modelMapper = mock(ModelMapper.class);
        restaurantOrderRepository = mock(RestaurantOrderRepository.class);
        restaurantOrderService = new RestaurantOrderService(restaurantOrderRepository, modelMapper);
        restaurantOrderDTO = mock(RestaurantOrderDTO.class);
        restaurantOrder = mock(RestaurantOrder.class);
    }


    @Test
    public void create_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenRestaurantOrderDTOIsGiven()
            throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException {
        //Arrange
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
        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(modelMapper.map(restaurantOrder, RestaurantOrderDTO.class)).thenReturn(restaurantOrderDTO);

        //Act
        RestaurantOrderDTO actual = restaurantOrderService.findById(id);

        //Assert
        assertEquals(restaurantOrderDTO, actual);
    }

    @Test
    public void findAll_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTOList_WhenRestaurantOrderExist() {
        //Arrange
        List<RestaurantOrder> restaurantOrderList = Arrays.asList(restaurantOrder);
        List<RestaurantOrderDTO> expected = Arrays.asList(restaurantOrderDTO);

        when(restaurantOrderRepository.findAll()).thenReturn(restaurantOrderList);
        //I ensure that every RestaurantOrder is returned as DTO through model mapper:
        when(modelMapper.map(any(RestaurantOrder.class), eq(RestaurantOrderDTO.class))).thenReturn(restaurantOrderDTO);

        //Act
        List<RestaurantOrderDTO> actual = restaurantOrderService.findAll();

        //Assert
        assertIterableEquals(expected, actual);
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderWithGivenIdNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.update(nonExistedId, restaurantOrderDTO));
    }

}