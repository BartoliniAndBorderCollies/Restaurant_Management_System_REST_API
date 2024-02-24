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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
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

    @Test
    public void findById_ShouldReturnInventoryItemDTOResponse_WhenCorrectInventoryIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        InventoryItem inventoryItem = new InventoryItem(); //I instantiate to avoid mocking because I would get id null
        inventoryItem.setId(1000L);
        InventoryItemDTOResponse expected = mock(InventoryItemDTOResponse.class);
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expected);

        //Act
        InventoryItemDTOResponse actual = inventoryItemService.findById(inventoryItem.getId());

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void findAll_ShouldReturnInventoryItemDTOResponseList_WhenInventoriesExist() {
        //Arrange
        InventoryItem inventoryItem = mock(InventoryItem.class);
        InventoryItemDTOResponse expectedResponse = mock(InventoryItemDTOResponse.class);
        List<InventoryItem> inventoryItems = Arrays.asList(inventoryItem);

        when(inventoryItemRepository.findAll()).thenReturn(inventoryItems);
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expectedResponse);

        //Act
        List<InventoryItemDTOResponse> actual = inventoryItemService.findAll();

        //Assert
        assertEquals(1, actual.size());
        assertEquals(expectedResponse, actual.get(0));
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenInventoryDoesNotExist() {
        //Arrange
        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest();
        inventoryItemDTORequest.setId(1000L);

        //Act & Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> inventoryItemService.update(inventoryItemDTORequest.getId(),
                inventoryItemDTORequest));
    }

    @Test
    public void update_ShouldReturnInventoryItemDTOResponse_WhenInventoryItemDTORequestAndItsIdAreGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest();
        inventoryItemDTORequest.setId(1000L);

        InventoryItem inventoryItem = mock(InventoryItem.class);
        InventoryItemDTOResponse expected = mock(InventoryItemDTOResponse.class);
        when(inventoryItemRepository.findById(inventoryItemDTORequest.getId())).thenReturn(Optional.of(inventoryItem));
        when(modelMapper.map(inventoryItem, InventoryItemDTOResponse.class)).thenReturn(expected);

        //Act
        InventoryItemDTOResponse actual = inventoryItemService.update(inventoryItemDTORequest.getId(), inventoryItemDTORequest);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void delete_ShouldThrowNotFoundInDatabaseException_WhenInventoryIdDoesNotExist() {
        //Arrange
        Long nonExistentId = 9999L; // This should be an id that doesn't exist in the database

        //Act & Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> inventoryItemService.delete(nonExistentId));
    }

    @Test
    public void delete_ShouldCallOnInventoryRepoExactlyOnce_WhenCorrectInventoryIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        InventoryItem inventoryItem = new InventoryItem(1L, null, 100, null);
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));

        //Act
        inventoryItemService.delete(inventoryItem.getId());

        //Assert
        verify(inventoryItemRepository, times(1)).delete(inventoryItem);
    }

    @Test
    public void delete_ShouldReturnResponseEntityWithAppropriateStatusCodeAndMessage_WhenCorrectInventoryIdIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        InventoryItem inventoryItem = new InventoryItem(1L, null, 100, null);
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));

        //Act
        ResponseEntity<?> actualResponse = inventoryItemService.delete(inventoryItem.getId());

        //Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Inventory item: " + inventoryItem.getName() + " has been deleted!", actualResponse.getBody());
    }

}