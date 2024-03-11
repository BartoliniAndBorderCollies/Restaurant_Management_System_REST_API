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
    private InventoryItem inventoryItemMock;
    private InventoryItemDTOResponse inventoryItemDTOResponseMock;
    private InventoryItem inventoryItemInstance;

    @BeforeEach
    public void generalSetUp() {
        inventoryItemRepository = mock(InventoryItemRepository.class);
        modelMapper = mock(ModelMapper.class);
        supplierRepository = mock(SupplierRepository.class);
        SupplierService supplierService = new SupplierService(supplierRepository, modelMapper);
        inventoryItemService = new InventoryItemService(inventoryItemRepository, supplierService, modelMapper);
    }

    @BeforeEach
    public void setUpInstances() {
        inventoryItemMock = mock(InventoryItem.class);
        inventoryItemDTOResponseMock = mock(InventoryItemDTOResponse.class);
        inventoryItemInstance = new InventoryItem(1L, null, 100, null, "Onion",
                "Onion", 0.39 );
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
        InventoryItemDTORequest inventoryItemDTORequest = mock(InventoryItemDTORequest.class);

        when(modelMapper.map(inventoryItemDTORequest, InventoryItem.class)).thenReturn(inventoryItemMock);
        when(inventoryItemRepository.save(inventoryItemMock)).thenReturn(inventoryItemMock);
        when(modelMapper.map(inventoryItemMock, InventoryItemDTOResponse.class)).thenReturn(inventoryItemDTOResponseMock);

        //Act
        InventoryItemDTOResponse inventoryItemDTOResponse = inventoryItemService.create(inventoryItemDTORequest);

        //Assert
        assertEquals(inventoryItemDTOResponseMock, inventoryItemDTOResponse);
    }

    @Test
    public void create_ShouldCallExactlyOnceOnInventoryItemRepo_WhenInventoryItemDTORequestIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        InventoryItemDTORequest inventoryItemDTORequest = mock(InventoryItemDTORequest.class);

        when(modelMapper.map(inventoryItemDTORequest, InventoryItem.class)).thenReturn(inventoryItemMock);
        when(inventoryItemRepository.save(inventoryItemMock)).thenReturn(inventoryItemMock);
        when(modelMapper.map(inventoryItemMock, InventoryItemDTOResponse.class)).thenReturn(inventoryItemDTOResponseMock);

        //Act
        inventoryItemService.create(inventoryItemDTORequest);

        //Assert
        verify(inventoryItemRepository, times(1)).save(inventoryItemMock);
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
        when(inventoryItemRepository.findById(inventoryItemInstance.getId())).thenReturn(Optional.of(inventoryItemInstance));
        when(modelMapper.map(inventoryItemInstance, InventoryItemDTOResponse.class)).thenReturn(inventoryItemDTOResponseMock);

        //Act
        InventoryItemDTOResponse actual = inventoryItemService.findById(inventoryItemInstance.getId());

        //Assert
        assertEquals(inventoryItemDTOResponseMock, actual);
    }

    @Test
    public void findAll_ShouldReturnInventoryItemDTOResponseList_WhenInventoriesExist() {
        //Arrange
        List<InventoryItem> inventoryItems = Arrays.asList(inventoryItemMock);

        when(inventoryItemRepository.findAll()).thenReturn(inventoryItems);
        when(modelMapper.map(inventoryItemMock, InventoryItemDTOResponse.class)).thenReturn(inventoryItemDTOResponseMock);

        //Act
        List<InventoryItemDTOResponse> actual = inventoryItemService.findAll();

        //Assert
        assertEquals(1, actual.size());
        assertEquals(inventoryItemDTOResponseMock, actual.get(0));
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

        when(inventoryItemRepository.findById(inventoryItemDTORequest.getId())).thenReturn(Optional.of(inventoryItemMock));
        when(modelMapper.map(inventoryItemMock, InventoryItemDTOResponse.class)).thenReturn(inventoryItemDTOResponseMock);

        //Act
        InventoryItemDTOResponse actual = inventoryItemService.update(inventoryItemDTORequest.getId(), inventoryItemDTORequest);

        //Assert
        assertEquals(inventoryItemDTOResponseMock, actual);
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
        when(inventoryItemRepository.findById(inventoryItemInstance.getId())).thenReturn(Optional.of(inventoryItemInstance));

        //Act
        inventoryItemService.delete(inventoryItemInstance.getId());

        //Assert
        verify(inventoryItemRepository, times(1)).delete(inventoryItemInstance);
    }

    @Test
    public void delete_ShouldReturnResponseEntityWithAppropriateStatusCodeAndMessage_WhenCorrectInventoryIdIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        when(inventoryItemRepository.findById(inventoryItemInstance.getId())).thenReturn(Optional.of(inventoryItemInstance));

        //Act
        ResponseEntity<?> actualResponse = inventoryItemService.delete(inventoryItemInstance.getId());

        //Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Inventory item: " + inventoryItemInstance.getName() + " has been deleted!", actualResponse.getBody());
    }
}