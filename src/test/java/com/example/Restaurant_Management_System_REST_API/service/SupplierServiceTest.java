package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    private SupplierService supplierService;
    private ModelMapper modelMapper;
    private SupplierRepository supplierRepository;

    @BeforeEach
    public void generalSetUp() {
        modelMapper = mock(ModelMapper.class);
        supplierRepository = mock(SupplierRepository.class);
        supplierService = new SupplierService(supplierRepository, modelMapper);
    }

    @Test
    public void add_ShouldThrowObjectAlreadyExistException_WhenSupplierAlreadyExist() {
        //Arrange
        ContactDetails contactDetails = new ContactDetails("test name", "test street", "test houseNumber", "test city",
                "test postalCode", "test telephoneNumber");
        Supplier supplier = new Supplier(1L, contactDetails, null);
        SupplierDTORequest supplierDTORequest = new SupplierDTORequest(1L, contactDetails, null);

        when(modelMapper.map(supplierDTORequest, Supplier.class)).thenReturn(supplier);
       when(supplierRepository.findByContactDetails_NameAndContactDetails_Street(supplier.getContactDetails().getName(),
                       supplier.getContactDetails().getStreet())).thenReturn(Optional.of(supplier));

        //Act
        //Assert
        assertThrows(ObjectAlreadyExistException.class, ()-> supplierService.add(supplierDTORequest));
    }

}