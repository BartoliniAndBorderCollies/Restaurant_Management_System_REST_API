package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
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
    private ModelMapper modelMapper;

    @Override
    public InventoryItemDTOResponse create(InventoryItemDTORequest inventoryItemDTORequest) {
        InventoryItem inventoryItem = modelMapper.map(inventoryItemDTORequest, InventoryItem.class);
        inventoryItemRepository.save(inventoryItem);

        return modelMapper.map(inventoryItem, InventoryItemDTOResponse.class);
    }
}
