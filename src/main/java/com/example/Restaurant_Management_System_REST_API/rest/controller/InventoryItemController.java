package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.service.InventoryItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/")
@AllArgsConstructor
public class InventoryItemController {

    private InventoryItemService inventoryItemService;

    @PostMapping("/add")
    public InventoryItemDTOResponse create (@RequestBody InventoryItemDTORequest inventoryItemDTORequest) {
        return inventoryItemService.create(inventoryItemDTORequest);
    }
}
