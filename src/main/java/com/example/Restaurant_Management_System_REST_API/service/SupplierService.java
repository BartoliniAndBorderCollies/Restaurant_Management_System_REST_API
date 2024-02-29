package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<SupplierDTOResponse> findAll () {
        List<SupplierDTOResponse> DTOlist = new ArrayList<>();

        supplierRepository.findAll().forEach(supplier ->
            DTOlist.add(modelMapper.map(supplier, SupplierDTOResponse.class)));

        return DTOlist;
    }

    public ResponseEntity<?> deleteById(Long id) throws NotFoundInDatabaseException {
        Supplier supplierToDelete = supplierRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(Supplier.class));

        supplierRepository.delete(supplierToDelete);

        return new ResponseEntity<>("Supplier: " + supplierToDelete.getId() + " has been deleted!",
                HttpStatus.OK);
    }

}
