package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class MenuRecordServiceTest {

    private MenuRecordService menuRecordService;
    private MenuRecordRepository menuRecordRepository;
    private ModelMapper modelMapper;
    private MenuRecordDTORequest menuRecordDTORequest;
    private MenuRecord menuRecord;
    private MenuRecordDTOResponse menuRecordDTOResponse;
    private Set<String> ingredients;

    @BeforeEach
    public void generalSetUp() {
        menuRecordRepository = mock(MenuRecordRepository.class);
        modelMapper = mock(ModelMapper.class);
        menuRecordService = new MenuRecordService(menuRecordRepository, modelMapper);

        ingredients = new HashSet<>();
        ingredients.add("water");
        ingredients.add("hops");
        ingredients.add("barley");
    }


    @Nested
    class nestedClassForCreateMethods {

        @BeforeEach
        public void setUpForCreateMethods() {
            menuRecordDTORequest = new MenuRecordDTORequest(1L, ingredients, Category.BEVERAGE, true);
            menuRecord = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);
            menuRecordDTOResponse = new MenuRecordDTOResponse(1L, ingredients, Category.BEVERAGE, false);
        }

        @Test
        public void create_ShouldInteractWithDependenciesCorrectly_WhenMenuRecordDTORequestIsGiven() {
            //Arrange
            when(modelMapper.map(menuRecordDTORequest, MenuRecord.class)).thenReturn(menuRecord);
            when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse);

            //Act
            MenuRecordDTOResponse actual = menuRecordService.create(menuRecordDTORequest);

            //Assert
            assertEquals(menuRecordDTOResponse, actual);
        }

        @Test
        public void create_ShouldReturnTypeMenuRecordDTOResponse_WhenMenuRecordDTORequestIsGiven() {
            //Arrange - takes from @BeforeEach
            when(modelMapper.map(menuRecordDTORequest, MenuRecord.class)).thenReturn(menuRecord);
            when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse);

            //Act
            MenuRecordDTOResponse actual = menuRecordService.create(menuRecordDTORequest);

            //Assert
            assertEquals(menuRecordDTOResponse, actual);
        }

        @Test
        public void create_ShouldCallExactlyOnceOnMenuRecordRepo_WhenMenuRecordDTORequestIsGiven() {
            //Arrange - takes from @BeforeEach
            when(modelMapper.map(menuRecordDTORequest, MenuRecord.class)).thenReturn(menuRecord);
            when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse);

            //Act
            menuRecordService.create(menuRecordDTORequest);

            //Assert
            verify(menuRecordRepository, times(1)).save(menuRecord);
        }
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordIdIsNotFound() {
        assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.findById(1L));
    }

    @Test
    public void findById_ShouldReturnMenuRecordDTOResponse_WhenMenuRecordIdIsFound() throws NotFoundInDatabaseException {
        //Arrange
        Long id = 1L;
        MenuRecord menuRecord1 = new MenuRecord(id, ingredients, Category.DESSERT, true);
        MenuRecordDTOResponse expected = new MenuRecordDTOResponse(id, ingredients, Category.DESSERT, true);

        when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord1));
        when(modelMapper.map(menuRecord1, MenuRecordDTOResponse.class)).thenReturn(expected);
        //Act
        MenuRecordDTOResponse actual = menuRecordService.findById(id);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void findAll_ShouldReturnListOfMenuRecordDTOResponse_WhenTheyExistInDatabase() {
        //Arrange
        MenuRecord menuRecord1 = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);
        MenuRecord menuRecord2 = new MenuRecord(2L, ingredients, Category.BEVERAGE, true);
        List<MenuRecord> listMenuRecords = Arrays.asList(menuRecord1, menuRecord2);
        when(menuRecordRepository.findAll()).thenReturn(listMenuRecords);

        MenuRecordDTOResponse menuRecordDTOResponse1 = new MenuRecordDTOResponse(1L, ingredients, Category.BEVERAGE, true);
        MenuRecordDTOResponse menuRecordDTOResponse2 = new MenuRecordDTOResponse(2L, ingredients, Category.BEVERAGE, true);
        when(modelMapper.map(menuRecord1, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
        when(modelMapper.map(menuRecord2, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse2);

        List<MenuRecordDTOResponse> expected = Arrays.asList(menuRecordDTOResponse1, menuRecordDTOResponse2);

        //Act
        List<MenuRecordDTOResponse> actual = menuRecordService.findAll();

        //Assert
        assertIterableEquals(expected, actual);
    }
}