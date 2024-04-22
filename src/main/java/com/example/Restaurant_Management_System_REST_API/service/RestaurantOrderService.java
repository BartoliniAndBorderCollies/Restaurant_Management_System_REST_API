package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantOrderService implements GenericBasicCrudOperations<RestaurantOrderDTO, RestaurantOrderDTO, Long> {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final ModelMapper modelMapper;
    private final TableService tableService;
    private final MenuRecordService menuRecordService;
    private final InventoryItemService inventoryItemService;

    @Override
    @Transactional
    public RestaurantOrderDTO create(RestaurantOrderDTO restaurantOrderDTO) throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException {
        //1. Check if client provided meals which he wants to order
        if (restaurantOrderDTO.getMenuRecords() == null)
            throw new NotFoundInDatabaseException(MenuRecord.class);

        RestaurantOrder restaurantOrder = modelMapper.map(restaurantOrderDTO, RestaurantOrder.class);

        //2. Check if these meals exist in restaurant menu
        checkIfMealExist(restaurantOrder);

        //3 check if there are enough ingredients
        if(!areThereEnoughIngredients(restaurantOrder))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not enough ingredients to make this order!");


        //4. If table is not null, meaning that it is not take away order check if such table exist
        if (restaurantOrder.getTable() != null)
            tableService.checkIfTableExist(restaurantOrder.getTable().getId());

        //5. Set the order time to current time
        restaurantOrder.setOrderTime(LocalDateTime.now());

        //6. Set the order status to PENDING
        restaurantOrder.setOrderStatus(OrderStatus.PENDING);


        //7. Update the stock amount

        //8. Set the total price for the order

        restaurantOrderRepository.save(restaurantOrder);

        return modelMapper.map(restaurantOrder, RestaurantOrderDTO.class);
    }

    private boolean areThereEnoughIngredients(RestaurantOrder restaurantOrder) throws NotFoundInDatabaseException {
        List<MenuRecord> listOfMealsWhichClientWantsToOrder = restaurantOrder.getMenuRecords();

        for (MenuRecord menuRecord : listOfMealsWhichClientWantsToOrder) {
            List<Ingredient> ingredients = menuRecord.getIngredients();
            for (Ingredient eachIngredient : ingredients) {
                double requiredIngredientQuantity = eachIngredient.getAmountRequired();
                InventoryItem inventoryItem = findByName(eachIngredient.getName());
                if (inventoryItem == null || inventoryItem.getAmount() - requiredIngredientQuantity < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private InventoryItem findByName(String name) throws NotFoundInDatabaseException {
        return inventoryItemService.findByName(name);
    }

    private void checkIfMealExist(RestaurantOrder restaurantOrder) throws NotFoundInDatabaseException {
        List<MenuRecord> managedMenuRecords = new ArrayList<>();
        for (MenuRecord menuRecord : restaurantOrder.getMenuRecords()) {
            MenuRecord managedMenuRecord = menuRecordService.findByName(menuRecord.getName());
            //If I don't do the below then these fetched records from line above (from database) flow in the air. They
            // become managed by the Hibernate session and I cannot save the RestaurantOrder because I got unsaved
            // transient instance, because they were created outside of this session.
            managedMenuRecords.add(managedMenuRecord);
        }
        restaurantOrder.setMenuRecords(managedMenuRecords);
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

        RestaurantOrder existingRestaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(RestaurantOrder.class));
        RestaurantOrder restaurantOrder = modelMapper.map(updatedOrderDTO, RestaurantOrder.class);

        Optional.ofNullable(restaurantOrder.getOrderStatus()).ifPresent(existingRestaurantOrder::setOrderStatus);

        if (restaurantOrder.getTable() != null) { //its easier to use here if instead of Optional.ofNullable, because
            //I would need to try-catch the block with RuntimeException class, because lambda is not caught
            //with checked exception.
            Table table = tableService.checkIfTableExist(restaurantOrder.getTable().getId());
            existingRestaurantOrder.setTable(table);
        }

        Optional.ofNullable(restaurantOrder.getMenuRecords()).ifPresent(existingRestaurantOrder::setMenuRecords);
        //TODO: finish this logic in the next branch

        restaurantOrderRepository.save(existingRestaurantOrder);

        return modelMapper.map(existingRestaurantOrder, RestaurantOrderDTO.class);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        RestaurantOrder restaurantOrderToBeDeleted = restaurantOrderRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(RestaurantOrder.class));

        restaurantOrderRepository.delete(restaurantOrderToBeDeleted);

        return new ResponseEntity<>("Order number " + restaurantOrderToBeDeleted.getId() + " has been deleted!", HttpStatus.OK);
    }
}
