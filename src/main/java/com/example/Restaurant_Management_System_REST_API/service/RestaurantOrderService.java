package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderRequestDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotEnoughIngredientsException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderMenuRecordRepository;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class RestaurantOrderService implements GenericBasicCrudOperations<RestaurantOrderResponseDTO, RestaurantOrderRequestDTO, Long> {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ModelMapper modelMapper;
    private final TableService tableService;
    private final MenuRecordService menuRecordService;
    private final InventoryItemService inventoryItemService;
    private final RestaurantOrderMenuRecordRepository restaurantOrderMenuRecordRepository; //TODO - should not be here
    private final RestaurantOrderMenuRecordService restaurantOrderMenuRecordService;

    @Override
    @Transactional
    public RestaurantOrderResponseDTO create(RestaurantOrderRequestDTO restaurantOrderDTO) throws NotFoundInDatabaseException,
            NotEnoughIngredientsException {
        //1. Check if client provided meals which he wants to order
        if (restaurantOrderDTO.getMenuRecords() == null)
            throw new NotFoundInDatabaseException(MenuRecord.class);

        RestaurantOrder restaurantOrder = modelMapper.map(restaurantOrderDTO, RestaurantOrder.class);

        //2. Check if these meals exist in restaurant menu
        checkIfMealIsOnRestaurantMenu(restaurantOrderDTO);

        //3 check if there are enough ingredients
        if (!areThereEnoughIngredients(restaurantOrderDTO)) //I should take restaurantOrderDTO in this method,
            //because I have the amount of portions there
            throw new NotEnoughIngredientsException();

        //4. If table is not null, meaning that it is not take away order check if such table exist
        if (restaurantOrder.getTable() != null)
            tableService.checkIfTableExist(restaurantOrder.getTable().getId());

        //5. Set the order time to current time
        restaurantOrder.setOrderTime(LocalDateTime.now());

        //6. Set the order status to PENDING
        restaurantOrder.setOrderStatus(OrderStatus.PENDING);

        //7. Update the stock amount
        updateStockAmount(restaurantOrderDTO); //I should take restaurantOrderDTO in this method,
        //because I have the amount of portions there

        //8. Set the total price for the order
        restaurantOrder.setTotalAmountToPay(countTotalPrice(restaurantOrderDTO)); //I should take restaurantOrderDTO in this method,
        //because I have the amount of portions there

        //9. Since RestaurantOrder is not an owning side anymore (because I have created intermediate entity called RestaurantOrderMenuRecord
        // to have portionsAmount) now I must set restaurant orders and other dependencies manually
        setRestaurantOrders(restaurantOrderDTO, restaurantOrder);

        restaurantOrderRepository.save(restaurantOrder);

        //because in DTOs I have List<MenuRecordForOrderDTO> and in RestaurantOrder I have List<RestaurantOrderMenuRecord>
        // and they have different things inside I need to manually map between those 2 instances
        return mapManuallyFromRestaurantOrderToRestaurantOrderResponseDTO(restaurantOrder);
    }

    private RestaurantOrderResponseDTO mapManuallyFromRestaurantOrderToRestaurantOrderResponseDTO(RestaurantOrder restaurantOrder) {
        RestaurantOrderResponseDTO restaurantOrderResponseDTO = new RestaurantOrderResponseDTO();
        restaurantOrderResponseDTO.setId(restaurantOrder.getId());
        restaurantOrderResponseDTO.setOrderTime(restaurantOrder.getOrderTime());
        restaurantOrderResponseDTO.setOrderStatus(restaurantOrder.getOrderStatus());
        restaurantOrderResponseDTO.setTable(modelMapper.map(restaurantOrder.getTable(), TableReservationDTO.class));
        restaurantOrderResponseDTO.setTelephoneNumber(restaurantOrder.getTelephoneNumber());
        restaurantOrderResponseDTO.setTotalAmountToPay(restaurantOrder.getTotalAmountToPay());

        List<MenuRecordForOrderDTO> menuRecordForOrderDTOS = getMenuRecordForOrderDTOS(restaurantOrder);

        restaurantOrderResponseDTO.setMenuRecords(menuRecordForOrderDTOS);
        return restaurantOrderResponseDTO;
    }

    private List<MenuRecordForOrderDTO> getMenuRecordForOrderDTOS(RestaurantOrder restaurantOrder) {
        List<MenuRecordForOrderDTO> menuRecordForOrderDTOS = new ArrayList<>();
        for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord : restaurantOrder.getRestaurantOrders()) {
            MenuRecord menuRecord = eachRestaurantOrderMenuRecord.getMenuRecord();
            Double portionsAmount = eachRestaurantOrderMenuRecord.getPortionsAmount();

            MenuRecordForOrderDTO menuRecordForOrderDTO = modelMapper.map(menuRecord, MenuRecordForOrderDTO.class);
            menuRecordForOrderDTO.setPortionsAmount(portionsAmount);

            menuRecordForOrderDTOS.add(menuRecordForOrderDTO);
        }
        return menuRecordForOrderDTOS;
    }


    private void setRestaurantOrders(RestaurantOrderRequestDTO restaurantOrderDTO, RestaurantOrder restaurantOrder)
            throws NotFoundInDatabaseException {
        List<RestaurantOrderMenuRecord> recordsList = new ArrayList<>();

        // Save the RestaurantOrder entity first
        RestaurantOrder savedRestaurantOrder = restaurantOrderRepository.save(restaurantOrder);

        for (MenuRecordForOrderDTO menuRecordForOrderDTO : restaurantOrderDTO.getMenuRecords()) {
            RestaurantOrderMenuRecord record = new RestaurantOrderMenuRecord();

            // Retrieve the existing MenuRecord from the database
            MenuRecord menuRecord = menuRecordService.findByName(menuRecordForOrderDTO.getName());

            // Set the existing MenuRecord to the record
            record.setMenuRecord(menuRecord);

            // Set the saved RestaurantOrder to the record
            record.setRestaurantOrder(savedRestaurantOrder);
            record.setPortionsAmount(menuRecordForOrderDTO.getPortionsAmount());

            // Save the record and fetch the saved entity
            RestaurantOrderMenuRecord savedRecord = restaurantOrderMenuRecordRepository.save(record);

            // Add the saved entity to the list
            recordsList.add(savedRecord);
        }
        // Update the savedRestaurantOrder with the recordsList
        savedRestaurantOrder.setRestaurantOrders(recordsList);
    }

    private void checkIfMealIsOnRestaurantMenu(RestaurantOrderRequestDTO restaurantOrderRequestDTO)
            throws NotFoundInDatabaseException {
        List<MenuRecordForOrderDTO> menuRecordForOrderDTOS = restaurantOrderRequestDTO.getMenuRecords();
        for (MenuRecordForOrderDTO eachMenuRecordDTO : menuRecordForOrderDTOS) {
            menuRecordService.findByName(eachMenuRecordDTO.getName());
        }
    }

    private boolean areThereEnoughIngredients(RestaurantOrderRequestDTO restaurantOrderRequestDTO) throws NotFoundInDatabaseException {
        List<MenuRecordForOrderDTO> listOfMealsWhichClientWantsToOrder = restaurantOrderRequestDTO.getMenuRecords();

        for (MenuRecordForOrderDTO menuRecordForOrderDTO : listOfMealsWhichClientWantsToOrder) {
            MenuRecord existingMenuRecord = menuRecordService.findByName(menuRecordForOrderDTO.getName());
            List<Ingredient> ingredients = existingMenuRecord.getIngredients();
            for (Ingredient eachIngredient : ingredients) {
                double requiredIngredientQuantity = eachIngredient.getAmountRequired() * menuRecordForOrderDTO.getPortionsAmount();
                InventoryItem inventoryItem = findByName(eachIngredient.getName());
                if (inventoryItem == null || inventoryItem.getAmount() - requiredIngredientQuantity < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateStockAmount(RestaurantOrderRequestDTO restaurantOrderRequestDTO) throws NotFoundInDatabaseException {
        List<MenuRecordForOrderDTO> listOfMealsWhichClientWantsToOrder = restaurantOrderRequestDTO.getMenuRecords();

        for (MenuRecordForOrderDTO menuRecordForOrderDTO : listOfMealsWhichClientWantsToOrder) {
            MenuRecord existingMenuRecord = menuRecordService.findByName(menuRecordForOrderDTO.getName());
            List<Ingredient> ingredients = existingMenuRecord.getIngredients();
            for (Ingredient eachIngredient : ingredients) {
                double requiredIngredientQuantity = eachIngredient.getAmountRequired() * menuRecordForOrderDTO.getPortionsAmount();
                InventoryItem inventoryItem = findByName(eachIngredient.getName());
                inventoryItem.setAmount(inventoryItem.getAmount() - requiredIngredientQuantity);
            }
        }
    }

    private double countTotalPrice(RestaurantOrderRequestDTO restaurantOrderRequestDTO) throws NotFoundInDatabaseException {
        List<MenuRecordForOrderDTO> allOrderedMealsDTO = restaurantOrderRequestDTO.getMenuRecords();
        //RestaurantOrderRequestDTO has a List<MenuRecordForOrderDTO>, each MenuRecordForOrderDTO has portions amount.
        //MenuRecord does not have portions amount (because each time when I save a MenuRecord to db it would be zero)
        //but MenuRecord has a price. However, price is not in MenuRecordForOrderDTO.
        //Therefore, I must combine them together. Price I take from MenuRecord, portions amount I take from MenuRecordForOrderDTO
        double totalPrice = 0;
        for (MenuRecordForOrderDTO eachMeal : allOrderedMealsDTO) {
            MenuRecord menuRecord = menuRecordService.findByName(eachMeal.getName());
            totalPrice += menuRecord.getPrice() * eachMeal.getPortionsAmount();
        }
        return totalPrice;
    }

    private InventoryItem findByName(String name) throws NotFoundInDatabaseException {
        return inventoryItemService.findByName(name);
    }

    @Override
    public RestaurantOrderResponseDTO findById(Long id) throws NotFoundInDatabaseException {
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(() -> new
                NotFoundInDatabaseException(RestaurantOrder.class));
        RestaurantOrderResponseDTO responseDTO = modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class);

        responseDTO.setMenuRecords(getMenuRecordForOrderDTOS(restaurantOrder));

        return responseDTO;
    }

    @Override
    public List<RestaurantOrderResponseDTO> findAll() {
        List<RestaurantOrderResponseDTO> orderDTOList = new ArrayList<>();
        restaurantOrderRepository.findAll().forEach(restaurantOrder -> {
            RestaurantOrderResponseDTO orderDto = modelMapper.map(restaurantOrder, RestaurantOrderResponseDTO.class);
            orderDto.setMenuRecords(getMenuRecordForOrderDTOS(restaurantOrder));
            orderDTOList.add(orderDto);

        });
        return orderDTOList;
    }

    @Override
    public RestaurantOrderResponseDTO update(Long id, RestaurantOrderRequestDTO updatedOrderDTO) throws NotFoundInDatabaseException {

        RestaurantOrder existingRestaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(RestaurantOrder.class));
        RestaurantOrder restaurantOrder = modelMapper.map(updatedOrderDTO, RestaurantOrder.class);

        Optional.ofNullable(restaurantOrder.getOrderStatus()).ifPresent(existingRestaurantOrder::setOrderStatus);
        Optional.ofNullable(restaurantOrder.getTelephoneNumber()).ifPresent(existingRestaurantOrder::setTelephoneNumber);

        if (restaurantOrder.getTable() != null) { //its easier to use here if instead of Optional.ofNullable, because
            //I would need to try-catch the block with RuntimeException class, because lambda is not caught
            //with checked exception.
            Table table = tableService.checkIfTableExist(restaurantOrder.getTable().getId());
            existingRestaurantOrder.setTable(table);
        }

        restaurantOrderRepository.save(existingRestaurantOrder);

        return modelMapper.map(existingRestaurantOrder, RestaurantOrderResponseDTO.class);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        RestaurantOrder restaurantOrderToBeDeleted = restaurantOrderRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(RestaurantOrder.class));

        restaurantOrderMenuRecordService.deleteRestaurantOrderMenuRecordsByRestaurantOrderId(id);
        restaurantOrderRepository.delete(restaurantOrderToBeDeleted);

        return new ResponseEntity<>("Order number " + restaurantOrderToBeDeleted.getId() + " has been deleted!", HttpStatus.OK);
    }
}
