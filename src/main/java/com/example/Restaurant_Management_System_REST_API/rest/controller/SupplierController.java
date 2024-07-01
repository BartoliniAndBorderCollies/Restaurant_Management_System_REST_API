package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import com.example.Restaurant_Management_System_REST_API.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@AllArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    /**
     * This method is used to add a supplier.
     * Access is restricted to the owner and manager.
     *
     * @param supplierDTO The request body containing the details of the supplier to be added.
     * @return The response body containing the details of the added supplier.
     * @throws ObjectAlreadyExistException If the supplier already exists.
     */
    @PostMapping("/add")
    public SupplierDTO add(@RequestBody SupplierDTO supplierDTO) throws ObjectAlreadyExistException {
        return supplierService.add(supplierDTO);
    }

    /**
     * This method is used to find all suppliers.
     * Access is granted to the owner, manager, and staff.
     *
     * @return A list containing the details of all suppliers.
     */
    @GetMapping("/findAll")
    public List<SupplierDTO> findAll() {
        return supplierService.findAll();
    }

    /**
     * This method is used to delete a supplier by its ID.
     * Access is restricted to the owner and manager.
     *
     * @param id The ID of the supplier to be deleted.
     * @return The ResponseDTO after deleting the supplier.
     * @throws NotFoundInDatabaseException If the supplier is not found in the database.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseDTO deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return supplierService.deleteById(id);
    }
}
