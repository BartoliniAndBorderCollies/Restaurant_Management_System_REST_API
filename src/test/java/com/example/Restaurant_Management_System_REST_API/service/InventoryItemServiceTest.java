package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

class InventoryItemServiceTest {

    private InventoryItemService inventoryItemService;
    private InventoryItemRepository inventoryItemRepository;
    private SupplierRepository supplierRepository;
    private ModelMapper modelMapper;

    @BeforeEach
    public void generalSetUp() {
        inventoryItemRepository = mock(InventoryItemRepository.class);
        modelMapper = mock(ModelMapper.class);
        supplierRepository = mock(SupplierRepository.class);
        inventoryItemService = new InventoryItemService(inventoryItemRepository, supplierRepository, modelMapper);
    }

    @Test
    public void create_ShouldThrowNotFoundInDatabaseException_WhenInventoryItemDoesNotExist() {
        //Arrange
        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest();
        Supplier supplier = new Supplier();
        ContactDetails contactDetails = new ContactDetails();

        contactDetails.setName("lala");
        contactDetails.setStreet("ulica");

        supplier.setContactDetails(contactDetails);
        inventoryItemDTORequest.setSupplier(supplier);

        when(supplierRepository.findByContactDetails_NameAndContactDetails_Street(anyString(), anyString())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> inventoryItemService.create(inventoryItemDTORequest));
    }

    @Test
    public void create_ShouldInteractWithDependenciesCorrectly_WhenInventoryItemDTORequestIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        InventoryItem inventoryItem = new InventoryItem(1L, null, 100, null);
        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest(1L, null, 100, null);
        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(1L, null, 100, null);

        when(modelMapper.map(inventoryItemDTORequest, InventoryItem.class)).thenReturn(inventoryItem);
        when(inventoryItemRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expected);

        //Act
        InventoryItemDTOResponse inventoryItemDTOResponse = inventoryItemService.create(inventoryItemDTORequest);

        //Assert
        assertEquals(expected, inventoryItemDTOResponse);
    }

}