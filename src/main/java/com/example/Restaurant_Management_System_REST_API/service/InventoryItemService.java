package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InventoryItemService implements GenericBasicCrudOperations<InventoryItemDTOResponse, InventoryItemDTORequest,
        Long> {

    private InventoryItemRepository inventoryItemRepository;
    private SupplierRepository supplierRepository;
    private ModelMapper modelMapper;

    @Override
    public InventoryItemDTOResponse create(InventoryItemDTORequest inventoryItemDTORequest) throws NotFoundInDatabaseException {

        //TODO: prepare SupplierController and service and check if below works with existing suppliers
       //Checking if provided supplier exists in database - if not -> throwing an exception
        if(inventoryItemDTORequest.getSupplier() != null) {
            supplierRepository.findByContactDetails_NameAndContactDetails_Street(inventoryItemDTORequest.getSupplier().getContactDetails().getName(),
                    inventoryItemDTORequest.getSupplier().getContactDetails().getStreet()).orElseThrow(() ->
                    new NotFoundInDatabaseException(Supplier.class));
        }

        InventoryItem inventoryItem = modelMapper.map(inventoryItemDTORequest, InventoryItem.class);
        inventoryItemRepository.save(inventoryItem);

        return modelMapper.map(inventoryItem, InventoryItemDTOResponse.class);
    }
}
