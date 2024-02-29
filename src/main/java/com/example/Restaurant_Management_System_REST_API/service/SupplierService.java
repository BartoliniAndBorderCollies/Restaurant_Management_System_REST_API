package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    public SupplierDTOResponse add(SupplierDTORequest supplierDTORequest) {
        Supplier supplier = modelMapper.map(supplierDTORequest, Supplier.class);
        supplierRepository.save(supplier);

        return modelMapper.map(supplier, SupplierDTOResponse.class);
    }

}