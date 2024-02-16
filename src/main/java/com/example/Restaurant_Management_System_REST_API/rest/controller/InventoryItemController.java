package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.InventoryItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/")
@AllArgsConstructor
public class InventoryItemController {

    private InventoryItemService inventoryItemService;

    @PostMapping("/add") // This will be done only by owner, manager and staff
    public InventoryItemDTOResponse create (@RequestBody InventoryItemDTORequest inventoryItemDTORequest)
            throws NotFoundInDatabaseException {
        return inventoryItemService.create(inventoryItemDTORequest);
    }

    @GetMapping("/find/{id}") // This will be done only by owner, manager and staff
    public InventoryItemDTOResponse findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return inventoryItemService.findById(id);
    }

    @GetMapping("/findAll") // This will be done only by owner, manager and staff
    public List<InventoryItemDTOResponse> findAll() {
        return inventoryItemService.findAll();
    }

    @PutMapping("/update/{id}")
    public InventoryItemDTOResponse update(@PathVariable Long id, @RequestBody InventoryItemDTORequest inventoryItemDTORequest)
            throws NotFoundInDatabaseException {
        return inventoryItemService.update(id, inventoryItemDTORequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return inventoryItemService.delete(id);
    }
}
