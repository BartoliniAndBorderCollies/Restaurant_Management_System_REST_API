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

    private final InventoryItemService inventoryItemService;

    /**
     * This method is used to create an inventory item.
     * Access is restricted to the owner, manager, and staff.
     *
     * @param inventoryItemDTORequest The request body containing the details of the inventory item to be created.
     * @return The response body containing the details of the created inventory item.
     * @throws NotFoundInDatabaseException If the requested item is not found in the database.
     */
    @PostMapping("/add")
    public InventoryItemDTOResponse create (@RequestBody InventoryItemDTORequest inventoryItemDTORequest)
            throws NotFoundInDatabaseException {
        return inventoryItemService.create(inventoryItemDTORequest);
    }

    /**
     * This method is used to find an inventory item by its ID.
     * Access is restricted to the owner, manager, and staff.
     *
     * @param id The ID of the inventory item to be found.
     * @return The response body containing the details of the found inventory item.
     * @throws NotFoundInDatabaseException If the requested item is not found in the database.
     */
    @GetMapping("/find/{id}")
    public InventoryItemDTOResponse findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return inventoryItemService.findById(id);
    }

    /**
     * This method is used to find all inventory items.
     * Access is restricted to the owner, manager, and staff.
     *
     * @return A list of response bodies containing the details of all the found inventory items.
     */
    @GetMapping("/findAll")
    public List<InventoryItemDTOResponse> findAll() {
        return inventoryItemService.findAll();
    }

    /**
     * This method is used to update an inventory item by its ID.
     * Access is restricted to the owner, manager, and staff.
     *
     * @param id The ID of the inventory item to be updated.
     * @param inventoryItemDTORequest The request body containing the updated details of the inventory item.
     * @return The response body containing the details of the updated inventory item.
     * @throws NotFoundInDatabaseException If the requested item is not found in the database.
     */
    @PutMapping("/update/{id}")
    public InventoryItemDTOResponse update(@PathVariable Long id, @RequestBody InventoryItemDTORequest inventoryItemDTORequest)
            throws NotFoundInDatabaseException {
        return inventoryItemService.update(id, inventoryItemDTORequest);
    }

    /**
     * This method is used to delete an inventory item by its ID.
     * Access is restricted to the owner, manager, and staff.
     *
     * @param id The ID of the inventory item to be deleted.
     * @return A ResponseEntity indicating the result of the deletion operation.
     * @throws NotFoundInDatabaseException If the requested item is not found in the database.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return inventoryItemService.delete(id);
    }
}
