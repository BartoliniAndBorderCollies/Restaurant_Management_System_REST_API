package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public InventoryItemDTOResponse create(InventoryItemDTORequest inventoryItemDTORequest) throws NotFoundInDatabaseException,
            ObjectAlreadyExistException {
        //Checking if provided supplier exists in database - if not -> throwing an exception
        Supplier supplier = supplierService.checkIfSupplierExist(inventoryItemDTORequest);

        InventoryItem inventoryItem = modelMapper.map(inventoryItemDTORequest, InventoryItem.class);

        //Checking if provided inventory item with the same supplier already exists in database
        checkIfThisInventoryItemWithThisSupplierAlreadyExists(inventoryItemDTORequest);

        inventoryItem.setSupplier(supplier);
        inventoryItemRepository.save(inventoryItem);

        return modelMapper.map(inventoryItem, InventoryItemDTOResponse.class);
    }

    private void checkIfThisInventoryItemWithThisSupplierAlreadyExists(InventoryItemDTORequest inventoryItemDTORequest) throws ObjectAlreadyExistException {
        Optional<InventoryItem> optionalInventoryItem = inventoryItemRepository.findByNameAndSupplierContactDetailsName(
                inventoryItemDTORequest.getName(),
                inventoryItemDTORequest.getSupplier().getContactDetails().getName()
        );

        if (optionalInventoryItem.isPresent()) {
            throw new ObjectAlreadyExistException(InventoryItem.class);
        }
    }

    @Override
    public InventoryItemDTOResponse findById(Long id) throws NotFoundInDatabaseException {
        InventoryItem inventoryItem = inventoryItemRepository.findById(id).orElseThrow(() ->
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

        Optional.of(inventoryItemDTORequest.getAmount()).ifPresent(inventoryDTOToBeUpdated::setAmount);
        Optional.ofNullable(inventoryItemDTORequest.getSupplier()).ifPresent(inventoryDTOToBeUpdated::setSupplier);
        Optional.ofNullable(inventoryItemDTORequest.getName()).ifPresent(inventoryDTOToBeUpdated::setName);
        Optional.ofNullable(inventoryItemDTORequest.getDescription()).ifPresent(inventoryDTOToBeUpdated::setDescription);
        Optional.ofNullable(inventoryItemDTORequest.getPrice()).ifPresent(inventoryDTOToBeUpdated::setPrice);

        inventoryItemRepository.save(modelMapper.map(inventoryDTOToBeUpdated, InventoryItem.class));

        return inventoryDTOToBeUpdated;
    }

    @Override
    public ResponseDTO delete(Long id) throws NotFoundInDatabaseException {
        InventoryItem inventoryToDelete = inventoryItemRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(InventoryItem.class));
        inventoryItemRepository.delete(inventoryToDelete);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Inventory item: " + inventoryToDelete.getName() + " has been deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }

    public InventoryItem findByName(String name) throws NotFoundInDatabaseException {
        return inventoryItemRepository.findByName(name).orElseThrow(() -> new NotFoundInDatabaseException(InventoryItem.class));
    }
}
