package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTOResponse;
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
    private ContactDetails contactDetails;
    private Supplier supplier;
    private SupplierDTORequest supplierDTORequest;
    private SupplierDTOResponse supplierDTOResponse;

    @BeforeEach
    public void generalSetUp() {
        modelMapper = mock(ModelMapper.class);
        supplierRepository = mock(SupplierRepository.class);
        supplierService = new SupplierService(supplierRepository, modelMapper);

        contactDetails = new ContactDetails("test name", "test street", "test houseNumber", "test city",
                "test postalCode", "test telephoneNumber");
        supplier = new Supplier(1L, contactDetails, null);
        supplierDTORequest = new SupplierDTORequest(1L, contactDetails, null);
        supplierDTOResponse = new SupplierDTOResponse(1L, contactDetails, null);
    }

    @Test
    public void add_ShouldThrowObjectAlreadyExistException_WhenSupplierAlreadyExist() {
        //Arrange
        when(modelMapper.map(supplierDTORequest, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.findByContactDetails_NameAndContactDetails_Street(supplier.getContactDetails().getName(),
                supplier.getContactDetails().getStreet())).thenReturn(Optional.of(supplier));

        //Act
        //Assert
        assertThrows(ObjectAlreadyExistException.class, () -> supplierService.add(supplierDTORequest));
    }

}