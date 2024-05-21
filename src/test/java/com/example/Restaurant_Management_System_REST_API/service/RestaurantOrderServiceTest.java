package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderRequestDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotEnoughIngredientsException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
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
    private static final String TELEPHONE_NUMBER = "888888888";
    private static final Long INVALID_MEAL_ID = 1L;
    private static final String INVALID_MEAL_NAME = "Not valid meal";
    private static final Double INVALID_PORTIONS_AMOUNT = 1.0;
    private static final Long NON_EXISTENT_ORDER_ID = 999L;

    @BeforeEach
    public void setUpEnvironment() {
        modelMapper = mock(ModelMapper.class);
        restaurantOrderRepository = mock(RestaurantOrderRepository.class);
        tableRepository = mock(TableRepository.class);
        TableService tableService = mock(TableService.class);
        MenuRecordRepository menuRecordRepository = mock(MenuRecordRepository.class);
        MenuRecordService menuRecordService = new MenuRecordService(menuRecordRepository, modelMapper);
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
    public void create_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordsIsNull() {
        // Arrange
        RestaurantOrderRequestDTO restaurantOrderDTO = new RestaurantOrderRequestDTO();
        restaurantOrderDTO.setMenuRecords(null);

        // Act and Assert
        assertThrows(NotFoundInDatabaseException.class, () -> restaurantOrderService.create(restaurantOrderDTO));
    }

    @Test
    public void create_ShouldThrowNotFoundInDatabaseException_WhenMealNotInMenu() {
        // Arrange
        RestaurantOrderRequestDTO restaurantOrderDTO = createRestaurantOrderDTOWithInvalidMeal();

        // Act and Assert
        assertThrows(NotFoundInDatabaseException.class, () -> restaurantOrderService.create(restaurantOrderDTO));
    }

    private RestaurantOrderRequestDTO createRestaurantOrderDTOWithInvalidMeal() {
        RestaurantOrderRequestDTO restaurantOrderDTO = new RestaurantOrderRequestDTO();
        restaurantOrderDTO.setMenuRecords(Arrays.asList(new MenuRecordForOrderDTO(INVALID_MEAL_ID, INVALID_MEAL_NAME, INVALID_PORTIONS_AMOUNT)));
        return restaurantOrderDTO;
    }

    @Test
    public void create_ShouldCallOnRepoExactlyTwice_WhenRestaurantOrderDTOIsGiven()
            throws NotFoundInDatabaseException, NotEnoughIngredientsException {
        //Arrange
        when(modelMapper.map(restaurantOrderRequestDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrderRepository.save(restaurantOrder)).thenReturn(restaurantOrder);

        //Act
        restaurantOrderService.create(restaurantOrderRequestDTO);

        //Assert
        verify(restaurantOrderRepository, times(2)).save(restaurantOrder);
    }

    //I am not able to make more methods for create because point 2 blocks it. It requires to have menuRecord
    //in repository, but I cannot do this because this is a unit test and I should not prepare instances in repository

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderNotExist() {
        //Arrange
        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.findById(NON_EXISTENT_ORDER_ID));
    }

    @Test
    public void findById_ShouldFindAndReturnRestaurantOrderDTO_WhenRestaurantOrderExists()
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
    public void findAll_ShouldFindAndReturnRestaurantOrderDTOList_WhenRestaurantOrdersExist() {
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
    public void update_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderNotExist() {
        //Arrange
        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.update(NON_EXISTENT_ORDER_ID, restaurantOrderRequestDTO));
    }

    @Test
    public void update_ShouldUpdateAndReturnRestaurantOrderDTO_WhenIdIsGivenAndRestaurantOrderExists()
            throws NotFoundInDatabaseException {
        //Arrange
        Table table = mock(Table.class);

        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(modelMapper.map(restaurantOrderRequestDTO, RestaurantOrder.class)).thenReturn(restaurantOrder);
        when(restaurantOrder.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(restaurantOrder.getTelephoneNumber()).thenReturn(TELEPHONE_NUMBER);
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
    public void delete_ShouldThrowNotFoundInDatabaseException_WhenRestaurantOrderNotExist() {
        //Arrange
        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> restaurantOrderService.delete(NON_EXISTENT_ORDER_ID));
    }

    @Test
    public void delete_ShouldReturnOkResponse_WhenRestaurantOrderExists()
            throws NotFoundInDatabaseException {
        //Arrange
        when(restaurantOrderRepository.findById(id)).thenReturn(Optional.ofNullable(restaurantOrder));
        when(restaurantOrder.getId()).thenReturn(1L);

        String expectedBody = "Order number " + restaurantOrder.getId() + " has been deleted!";
        HttpStatus expectedStatus = HttpStatus.OK;

        //Act
        ResponseEntity<?> actual = restaurantOrderService.delete(id);

        //Assert
        assertEquals(expectedBody, actual.getBody());
        assertEquals(expectedStatus, actual.getStatusCode());
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