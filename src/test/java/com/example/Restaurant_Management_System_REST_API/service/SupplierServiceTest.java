package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    private SupplierService supplierService;
    private ModelMapper modelMapper;
    private SupplierRepository supplierRepository;
    private ContactDetails contactDetails;
    private Supplier supplier;
    private SupplierDTO supplierDTO;


    @BeforeEach
    public void generalSetUp() {
        modelMapper = mock(ModelMapper.class);
        supplierRepository = mock(SupplierRepository.class);
        supplierService = new SupplierService(supplierRepository, modelMapper);

        contactDetails = new ContactDetails("test name", "test street", "test houseNumber", "test city",
                "test postalCode", "test telephoneNumber");
        supplier = new Supplier(1L, contactDetails, null);
        supplierDTO = new SupplierDTO(1L, contactDetails, null);
        supplierDTO = new SupplierDTO(1L, contactDetails, null);
    }

    @Test
    public void add_ShouldThrowObjectAlreadyExistException_WhenSupplierAlreadyExist() {
        //Arrange
        when(modelMapper.map(supplierDTO, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.findByContactDetails_NameAndContactDetails_Street(supplier.getContactDetails().getName(),
                supplier.getContactDetails().getStreet())).thenReturn(Optional.of(supplier));

        //Act
        //Assert
        assertThrows(ObjectAlreadyExistException.class, () -> supplierService.add(supplierDTO));
    }

    @Test
    public void add_ShouldMapFindSaveMapAgainAndReturnDTO_WhenSupplierDTORequestIsGiven() throws ObjectAlreadyExistException {
        //Arrange
        when(modelMapper.map(supplierDTO, Supplier.class)).thenReturn(supplier);
        when(supplierRepository.findByContactDetails_NameAndContactDetails_Street(supplier.getContactDetails().getName(),
                supplier.getContactDetails().getStreet())).thenReturn(Optional.empty());
        when(supplierRepository.save(supplier)).thenReturn(supplier);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        SupplierDTO expected = new SupplierDTO(1L, contactDetails, null);

        //Act
        SupplierDTO actualResponse = supplierService.add(supplierDTO);

        //Assert
        assertEquals(expected, actualResponse);
    }

    @Test
    public void findAll_ShouldReturnSupplierDTOResponseList_WhenSuppliersExist() {
        //Arrange
        List<Supplier> supplierList = Arrays.asList(supplier);

        when(supplierRepository.findAll()).thenReturn(supplierList);
        when(modelMapper.map(supplier, SupplierDTO.class)).thenReturn(supplierDTO);

        //Act
        List<SupplierDTO> actual = supplierService.findAll();

        //Assert
        assertEquals(1, actual.size());
        assertEquals(supplierDTO, actual.get(0));
    }

    @Test
    public void deleteById_ShouldThrowNotFoundInDatabaseException_WhenSupplierDoesNotExist() {
        //Arrange
        Long notExistedId = 999L;
        when(supplierRepository.findById(notExistedId)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> supplierService.deleteById(notExistedId));
    }

    @Test
    public void deleteById_ShouldCallOnSupplierRepositoryExactlyOnce_WhenSupplierIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        supplierRepository.save(supplier);
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        //Act
        supplierService.deleteById(supplier.getId());

        //Assert
        verify(supplierRepository, times(1)).delete(supplier);
    }

    @Test
    public void deleteById_ShouldReturnResponseDTOWithAppropriateMessage_WhenSupplierIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        supplierRepository.save(supplier);
        when(supplierRepository.findById(supplier.getId())).thenReturn(Optional.of(supplier));

        //Act
        ResponseDTO actualResponse = supplierService.deleteById(supplier.getId());

        //Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatus());
        assertEquals("Supplier: " + supplier.getId() + " has been deleted!", actualResponse.getMessage());
    }
}