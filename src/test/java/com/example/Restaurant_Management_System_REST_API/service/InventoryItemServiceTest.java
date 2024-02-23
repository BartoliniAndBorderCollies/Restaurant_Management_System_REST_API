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
import static org.mockito.Mockito.*;

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
    public void create_ShouldThrowNotFoundInDatabaseException_WhenSupplierDoesNotExist() {
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
        InventoryItem inventoryItem = mock(InventoryItem.class);
        InventoryItemDTORequest inventoryItemDTORequest = mock(InventoryItemDTORequest.class);
        InventoryItemDTOResponse expected = mock(InventoryItemDTOResponse.class);

        when(modelMapper.map(inventoryItemDTORequest, InventoryItem.class)).thenReturn(inventoryItem);
        when(inventoryItemRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expected);

        //Act
        InventoryItemDTOResponse inventoryItemDTOResponse = inventoryItemService.create(inventoryItemDTORequest);

        //Assert
        assertEquals(expected, inventoryItemDTOResponse);
    }

    @Test
    public void create_ShouldCallExactlyOnceOnInventoryItemRepo_WhenInventoryItemDTORequestIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        InventoryItem inventoryItem = mock(InventoryItem.class);
        InventoryItemDTORequest inventoryItemDTORequest = mock(InventoryItemDTORequest.class);
        InventoryItemDTOResponse expected = mock(InventoryItemDTOResponse.class);

        when(modelMapper.map(inventoryItemDTORequest, InventoryItem.class)).thenReturn(inventoryItem);
        when(inventoryItemRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expected);

        //Act
        InventoryItemDTOResponse inventoryItemDTOResponse = inventoryItemService.create(inventoryItemDTORequest);

        //Assert
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenInventoryDoesNotExist() {
        //Arrange
        Long nonExistentId = 9999L; // This should be an id that doesn't exist in the database

        //Act & Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> inventoryItemService.findById(nonExistentId));
    }

}