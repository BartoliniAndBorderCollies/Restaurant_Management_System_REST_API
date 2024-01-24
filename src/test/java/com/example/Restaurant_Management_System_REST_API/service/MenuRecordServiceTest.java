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
    private Long id;
    private MenuRecordDTOResponse menuRecordDTOResponse1;

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

    @Nested
    class otherTestMethods {

        @BeforeEach
        public void setUp() {
            id = 1L;
            menuRecordDTOResponse1 = new MenuRecordDTOResponse(id, ingredients, Category.DESSERT, true);
        }

        @Test
        public void findById_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordIdIsNotFound() {
            assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.findById(id));
        }

        @Test
        public void findById_ShouldReturnMenuRecordDTOResponse_WhenMenuRecordIdIsFound() throws NotFoundInDatabaseException {
            //Arrange
            MenuRecord menuRecord1 = new MenuRecord(id, ingredients, Category.DESSERT, true);

            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord1));
            when(modelMapper.map(menuRecord1, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
            //Act
            MenuRecordDTOResponse actual = menuRecordService.findById(id);

            //Assert
            assertEquals(menuRecordDTOResponse1, actual);
        }

        @Test
        public void findAll_ShouldReturnListOfMenuRecordDTOResponse_WhenTheyExistInDatabase() {
            //Arrange
            MenuRecord menuRecord1 = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);
            MenuRecord menuRecord2 = new MenuRecord(2L, ingredients, Category.BEVERAGE, true);
            List<MenuRecord> listMenuRecords = Arrays.asList(menuRecord1, menuRecord2);
            when(menuRecordRepository.findAll()).thenReturn(listMenuRecords);

            MenuRecordDTOResponse menuRecordDTOResponse2 = new MenuRecordDTOResponse(2L, ingredients, Category.BEVERAGE, true);
            when(modelMapper.map(menuRecord1, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
            when(modelMapper.map(menuRecord2, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse2);

            List<MenuRecordDTOResponse> expected = Arrays.asList(menuRecordDTOResponse1, menuRecordDTOResponse2);

            //Act
            List<MenuRecordDTOResponse> actual = menuRecordService.findAll();

            //Assert
            assertIterableEquals(expected, actual);
        }

        @Test
        public void update_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordIdIsNotFound() {
            //Arrange
            MenuRecordDTORequest menuRecordDTORequest1 = new MenuRecordDTORequest(1L, ingredients, Category.FOR_KIDS, true);

            //Assert
            assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.update(1L, menuRecordDTORequest1));
        }

        @Test
        public void update_ShouldReturnExpectedDTOResponse_WhenIdAndDTOAreGiven() throws NotFoundInDatabaseException {
            //Arrange - takes from setUp()
            MenuRecord menuRecord1 = new MenuRecord(id, ingredients, Category.DESSERT, true);
            MenuRecordDTORequest menuRecordDTORequest1 = new MenuRecordDTORequest(id, ingredients, Category.DESSERT, true);

            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord1));
            when(modelMapper.map(menuRecord1, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
            when(modelMapper.map(menuRecordDTOResponse1, MenuRecord.class)).thenReturn(menuRecord1);
            when(menuRecordRepository.save(menuRecord1)).thenReturn(menuRecord1);

            //Act
            MenuRecordDTOResponse actual = menuRecordService.update(id, menuRecordDTORequest1);

            //Assert
            assertEquals(menuRecordDTOResponse1, actual);
        }

        @Test
        public void delete_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordIdIsNotFound() {
            //Assert
            assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.delete(id));
        }

        @Test
        public void delete_ShouldCallOnRepoExactlyOnce_WhenMenuRecordIsGiven() throws NotFoundInDatabaseException {
            //Arrange
            MenuRecord menuRecord1 = new MenuRecord();
            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord1));

            //Act
            menuRecordService.delete(id);

            //Assert
            verify(menuRecordRepository, times(1)).delete(menuRecord1);
        }


    }
}