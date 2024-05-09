package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderRequestDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotEnoughIngredientsException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderMenuRecordRepository;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
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
    private RestaurantOrderResponseDTO restaurantOrderResponseDTO;
    private RestaurantOrder restaurantOrder;
    private Long id;
    private TableRepository tableRepository;
    private RestaurantOrderRequestDTO restaurantOrderRequestDTO;

    @BeforeEach
    public void setUpEnvironment() {
        modelMapper = mock(ModelMapper.class);
        restaurantOrderRepository = mock(RestaurantOrderRepository.class);
        tableRepository = mock(TableRepository.class);
        TableService tableService = mock(TableService.class);
        MenuRecordService menuRecordService = mock(MenuRecordService.class);
        InventoryItemService inventoryItemService = mock(InventoryItemService.class);
        RestaurantOrderMenuRecordService restaurantOrderMenuRecordService = mock(RestaurantOrderMenuRecordService.class);
        restaurantOrderService = new RestaurantOrderService(restaurantOrderRepository, modelMapper, tableService,
                menuRecordService, inventoryItemService, restaurantOrderMenuRecordService);
        restaurantOrderResponseDTO = mock(RestaurantOrderResponseDTO.class);
        restaurantOrderRequestDTO = mock(RestaurantOrderRequestDTO.class);
        restaurantOrder = mock(RestaurantOrder.class);
        id = 1L;
    }


    @Test
    public void create_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenRestaurantOrderDTOIsGiven()
            throws NotFoundInDatabaseException, NotEnoughIngredientsException {
        //Arrange
        when(modelMapper.map(restaurantOrderResponseDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);
        when(modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class)).thenReturn(restaurantOrderResponseDTO);

        //Act
        RestaurantOrderResponseDTO actual = restaurantOrderService.create(restaurantOrderRequestDTO);

        //Assert
        assertEquals(restaurantOrderResponseDTO, actual);
    }

    @Test
    public void create_ShouldCallOnRepoExactlyOnce_WhenRestaurantOrderDTOIsGiven()
            throws NotFoundInDatabaseException, NotEnoughIngredientsException {
        //Arrange
        when(modelMapper.map(restaurantOrderResponseDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);
        when(modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class)).thenReturn(restaurantOrderResponseDTO);

        //Act
        restaurantOrderService.create(restaurantOrderRequestDTO);

        //Assert
        verify(restaurantOrderRepository, times(1)).save(restaurantOrder);
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
        when(modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class)).thenReturn(restaurantOrderResponseDTO);

        //Act
        RestaurantOrderResponseDTO actual = restaurantOrderService.findById(id);

        //Assert
        assertEquals(restaurantOrderResponseDTO, actual);
    }

    @Test
    public void findAll_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTOList_WhenRestaurantOrderExist() {
        //Arrange
        List<RestaurantOrder> restaurantOrderList = Arrays.asList(restaurantOrder);
        List<RestaurantOrderResponseDTO> expected = Arrays.asList(restaurantOrderResponseDTO);

        when(restaurantOrderRepository.findAll()).thenReturn(restaurantOrderList);
        //I ensure that every RestaurantOrder is returned as DTO through model mapper:
        when(modelMapper.map(any(RestaurantOrder.class), eq(RestaurantOrderResponseDTO.class))).thenReturn(restaurantOrderResponseDTO);

        //Act
        List<RestaurantOrderResponseDTO> actual = restaurantOrderService.findAll();

        //Assert
        assertIterableEquals(expected, actual);
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderWithGivenIdNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.update(nonExistedId, restaurantOrderRequestDTO));
    }

    @Test
    public void update_ShouldInteractWithDependenciesCorrectlyAndReturnRestaurantOrderDTO_WhenIdAndRestaurantOrderDTOsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Table table = mock(Table.class);
        String telephoneNumber = "888888888";

        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(modelMapper.map(restaurantOrderRequestDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrder.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(restaurantOrder.getTelephoneNumber()).thenReturn(telephoneNumber);
        when(restaurantOrder.getTable()).thenReturn(table);
        when(tableRepository.findById(anyLong())).thenReturn(Optional.of(table));
        restaurantOrder.setTable(table);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);
        when(modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class)).thenReturn(restaurantOrderResponseDTO);

        RestaurantOrderResponseDTO expected = restaurantOrderResponseDTO;

        //Act
        RestaurantOrderResponseDTO actual = restaurantOrderService.update(id, restaurantOrderRequestDTO);

        //Assert
        assertEquals(expected, actual);
        verify(restaurantOrder).setOrderStatus(OrderStatus.PENDING);
        verify(restaurantOrder).setTable(table);
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

    @Test
    public void delete_ShouldCallOnRepositoryExactlyOnce_WhenRestaurantOrderIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));

        //Act
        restaurantOrderService.delete(id);

        //Assert
        verify(restaurantOrderRepository, times(1)).delete(restaurantOrder);
    }
}