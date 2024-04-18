package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    private final TableService tableService;
    private final MenuRecordService menuRecordService;

    @Override
    public RestaurantOrderDTO create(RestaurantOrderDTO restaurantOrderDTO) throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException {
        if(restaurantOrderDTO.getMenuRecords() == null) {
            throw new NotFoundInDatabaseException(MenuRecord.class);
        }

        RestaurantOrder restaurantOrder = modelMapper.map(restaurantOrderDTO, RestaurantOrder.class);
        checkIfMealExist(restaurantOrder);
        tableService.checkIfTableExist(restaurantOrder.getTable().getId());
        //TODO: checkIfCustomerExist
        //TODO: update the stock amount

        restaurantOrderRepository.save(restaurantOrder);

        return modelMapper.map(restaurantOrder, RestaurantOrderDTO.class);
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

        RestaurantOrder existingRestaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(RestaurantOrder.class));
        RestaurantOrder restaurantOrder = modelMapper.map(updatedOrderDTO, RestaurantOrder.class);

        Optional.ofNullable(restaurantOrder.getOrderStatus()).ifPresent(existingRestaurantOrder::setOrderStatus);

        if(restaurantOrder.getTable() != null) { //its easier to use here if instead of Optional.ofNullable, because
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
        RestaurantOrder restaurantOrderToBeDeleted = restaurantOrderRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(RestaurantOrder.class));

        restaurantOrderRepository.delete(restaurantOrderToBeDeleted);

        return new ResponseEntity<>("Order number " + restaurantOrderToBeDeleted.getId() + " has been deleted!", HttpStatus.OK);
    }
}
