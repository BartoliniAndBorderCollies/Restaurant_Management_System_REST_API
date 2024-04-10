package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


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
    private Long id;

    @BeforeEach
    public void setUpEnvironment() {
        modelMapper = mock(ModelMapper.class);
        restaurantOrderRepository = mock(RestaurantOrderRepository.class);
        restaurantOrderService = new RestaurantOrderService(restaurantOrderRepository, modelMapper);
        restaurantOrderDTO = mock(RestaurantOrderDTO.class);
        restaurantOrder = mock(RestaurantOrder.class);
        id = 1L;
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

    //TODO: this below test will need to be updated after merging two new branches
    @Test
    public void update_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenIdAndRestaurantOrderDTOsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Table table = mock(Table.class);
        MenuRecord mockMenuRecord = mock(MenuRecord.class);
        List<MenuRecord> menuRecordList = Arrays.asList(mockMenuRecord);

        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(modelMapper.map(restaurantOrderDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrder.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(restaurantOrder.getTable()).thenReturn(table);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);
        when(modelMapper.map(restaurantOrder, RestaurantOrderDTO.class)).thenReturn(restaurantOrderDTO);
        when(restaurantOrder.getMenuRecords()).thenReturn(menuRecordList);


        RestaurantOrderDTO expected = restaurantOrderDTO;

        //Act
        RestaurantOrderDTO actual = restaurantOrderService.update(id, restaurantOrderDTO);

        //Assert
        assertEquals(expected, actual);
        verify(restaurantOrder).setOrderStatus(OrderStatus.PENDING);
        verify(restaurantOrder).setTable(table);
        verify(restaurantOrder).setMenuRecords(menuRecordList);
    }

    @Test
    public void delete_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderWithGivenIdNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.delete(nonExistedId));
    }

    @Test
    public void delete_ShouldInteractWithDependenciesCorrectlyAndReturnResponseEntity_WhenRestaurantOrderIdIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(restaurantOrder.getId()).thenReturn(1L);
        ResponseEntity<?> expected = new ResponseEntity<>("Order number " + restaurantOrder.getId() +
                " has been deleted!", HttpStatus.OK);

        //Act
        ResponseEntity<?> actual = restaurantOrderService.delete(id);

        //Assert
        assertEquals(expected, actual);
    }



}