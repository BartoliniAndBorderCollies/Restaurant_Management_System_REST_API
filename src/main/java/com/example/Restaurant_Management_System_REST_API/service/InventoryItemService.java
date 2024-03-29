package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
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
public class InventoryItemService implements GenericBasicCrudOperations<InventoryItemDTOResponse, InventoryItemDTORequest,
        Long> {

    private final InventoryItemRepository inventoryItemRepository;
    private final SupplierService supplierService;
    private final ModelMapper modelMapper;

    @Override
    public InventoryItemDTOResponse create(InventoryItemDTORequest inventoryItemDTORequest) throws NotFoundInDatabaseException {

       //Checking if provided supplier exists in database - if not -> throwing an exception
       Supplier supplier = supplierService.checkIfSupplierExist(inventoryItemDTORequest);

        InventoryItem inventoryItem = modelMapper.map(inventoryItemDTORequest, InventoryItem.class);
        inventoryItem.setSupplier(supplier);
        inventoryItemRepository.save(inventoryItem);

        return modelMapper.map(inventoryItem, InventoryItemDTOResponse.class);
    }

    @Override
    public InventoryItemDTOResponse findById(Long id) throws NotFoundInDatabaseException {
        InventoryItem inventoryItem = inventoryItemRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(InventoryItem.class));

        return modelMapper.map(inventoryItem, InventoryItemDTOResponse.class);
    }

    @Override
    public List<InventoryItemDTOResponse> findAll() {
        List<InventoryItemDTOResponse> listInventoryDTOs = new ArrayList<>();

        inventoryItemRepository.findAll().forEach(inventoryItem ->
                listInventoryDTOs.add(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)));

        return listInventoryDTOs;
    }

    @Override
    public InventoryItemDTOResponse update(Long id, InventoryItemDTORequest inventoryItemDTORequest)
            throws NotFoundInDatabaseException {
        InventoryItemDTOResponse inventoryDTOToBeUpdated = findById(id);

        Optional.ofNullable(inventoryItemDTORequest.getDeliveryDate()).ifPresent(inventoryDTOToBeUpdated::setDeliveryDate);
        Optional.of(inventoryItemDTORequest.getStockAmount()).ifPresent(inventoryDTOToBeUpdated::setStockAmount);
        Optional.ofNullable(inventoryItemDTORequest.getSupplier()).ifPresent(inventoryDTOToBeUpdated::setSupplier);
        Optional.ofNullable(inventoryItemDTORequest.getName()).ifPresent(inventoryDTOToBeUpdated::setName);
        Optional.ofNullable(inventoryItemDTORequest.getDescription()).ifPresent(inventoryDTOToBeUpdated::setDescription);
        Optional.ofNullable(inventoryItemDTORequest.getPrice()).ifPresent(inventoryDTOToBeUpdated::setPrice);

        inventoryItemRepository.save(modelMapper.map(inventoryDTOToBeUpdated, InventoryItem.class));

        return inventoryDTOToBeUpdated;
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        InventoryItem inventoryToDelete = inventoryItemRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(InventoryItem.class));
        inventoryItemRepository.delete(inventoryToDelete);

        return new ResponseEntity<>("Inventory item: " + inventoryToDelete.getName() + " has been deleted!", HttpStatus.OK);
    }
}
