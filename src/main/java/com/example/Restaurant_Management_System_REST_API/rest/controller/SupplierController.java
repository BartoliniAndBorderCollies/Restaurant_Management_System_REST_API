package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTOResponse;
import com.example.Restaurant_Management_System_REST_API.service.SupplierService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
@AllArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    //This will be done only by owner, manager and staff
    @PostMapping("/add")
    public SupplierDTOResponse add(@RequestBody SupplierDTORequest supplierDTORequest) {
        return supplierService.add(supplierDTORequest);
    }

    @GetMapping("/findAll")
    public List<SupplierDTOResponse> findAll() {
        return supplierService.findAll();
    }

}