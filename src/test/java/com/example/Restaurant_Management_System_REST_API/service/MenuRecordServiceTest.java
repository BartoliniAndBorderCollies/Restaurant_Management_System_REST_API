package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Ingredient;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    private List<Ingredient> ingredients;
    private Long id;
    private MenuRecordDTOResponse menuRecordDTOResponse1;

    @BeforeEach
    public void generalSetUp() {
        menuRecordRepository = mock(MenuRecordRepository.class);
        modelMapper = mock(ModelMapper.class);
        menuRecordService = new MenuRecordService(menuRecordRepository, modelMapper);

        ingredients = new ArrayList<>();
        Ingredient potatoes = new Ingredient("Potatoes", 0.3);
        Ingredient chop = new Ingredient("Chop", 0.2);
        Ingredient pickledCabbage = new Ingredient("Pickled cabbage", 0.2);
        ingredients.add(potatoes);
        ingredients.add(chop);
        ingredients.add(pickledCabbage);
    }


    @Nested
    class nestedClassForCreateMethods {

        @BeforeEach
        public void setUpForCreateMethods() {
            menuRecordDTORequest = new MenuRecordDTORequest("DTO request", "DTO description", 10.0, null,
                    ingredients, Category.FOR_KIDS, true);
            menuRecord = new MenuRecord(ingredients, Category.BEVERAGE, "Drink", "Thirsty very much!",
                    6.0, true);
            menuRecordDTOResponse = new MenuRecordDTOResponse(1L, "Lech beer", ingredients, Category.BEVERAGE, false, new ArrayList<>());
        }

        @Test
        public void create_ShouldInteractWithDependenciesCorrectly_WhenMenuRecordDTORequestIsGiven() throws NotFoundInDatabaseException {
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
        public void create_ShouldReturnTypeMenuRecordDTOResponse_WhenMenuRecordDTORequestIsGiven() throws NotFoundInDatabaseException {
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
        public void create_ShouldCallExactlyOnceOnMenuRecordRepo_WhenMenuRecordDTORequestIsGiven() throws NotFoundInDatabaseException {
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
            menuRecordDTOResponse1 = new MenuRecordDTOResponse(id, "Lech beer", ingredients, Category.DESSERT,
                    true, new ArrayList<>());
            menuRecord = new MenuRecord(ingredients, Category.DESSERT, "Lovely dessert!", "mhmmm",
                    6.5, true);
        }

        @Test
        public void findById_ShouldThrowNotFoundInDatabaseException_WhenMenuRecordIdIsNotFound() {
            assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.findById(id));
        }

        @Test
        public void findById_ShouldReturnMenuRecordDTOResponse_WhenMenuRecordIdIsFound() throws NotFoundInDatabaseException {
            //Arrange - takes from setUp()

            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord));
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
            //Act
            MenuRecordDTOResponse actual = menuRecordService.findById(id);

            //Assert
            assertEquals(menuRecordDTOResponse1, actual);
        }

        @Test
        public void findAll_ShouldReturnListOfMenuRecordDTOResponse_WhenTheyExistInDatabase() {
            //Arrange
            MenuRecord menuRecord2 = new MenuRecord(ingredients, Category.BEVERAGE, "Little drink",
                    "So good", 4.39, true);
            List<MenuRecord> listMenuRecords = Arrays.asList(menuRecord, menuRecord2);
            when(menuRecordRepository.findAll()).thenReturn(listMenuRecords);

            MenuRecordDTOResponse menuRecordDTOResponse2 = new MenuRecordDTOResponse(2L, "Lech beer", ingredients,
                    Category.BEVERAGE, true, new ArrayList<>());
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
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
            MenuRecordDTORequest menuRecordDTORequest1 = new MenuRecordDTORequest("DTO request", "DTO description", 10.0, null,
                    ingredients, Category.FOR_KIDS, true);

            //Assert
            assertThrows(NotFoundInDatabaseException.class, () -> menuRecordService.update(1L, menuRecordDTORequest1));
        }

        @Test
        public void update_ShouldReturnExpectedDTOResponse_WhenIdAndDTOAreGiven() throws NotFoundInDatabaseException {
            //Arrange - takes from setUp()
            MenuRecordDTORequest menuRecordDTORequest1 = new MenuRecordDTORequest("DTO request",
                    "DTO description", 10.0, null, ingredients, Category.FOR_KIDS, true);

            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord));
            when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(menuRecordDTOResponse1);
            when(modelMapper.map(menuRecordDTOResponse1, MenuRecord.class)).thenReturn(menuRecord);
            when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);

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
            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord));

            //Act
            menuRecordService.delete(id);

            //Assert
            verify(menuRecordRepository, times(1)).delete(menuRecord);
        }

        @Test
        public void delete_ShouldReturnResponsEntityWithStatusOkAndAppropriateMessage_WhenMenuRecordIsDeleted()
                throws NotFoundInDatabaseException {
            //Arrange
            when(menuRecordRepository.findById(id)).thenReturn(Optional.of(menuRecord));

            //Act
            ResponseEntity<?> actualResponse = menuRecordService.delete(id);

            //Assert
            assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
            assertEquals("Menu record has been deleted!", actualResponse.getBody());
        }
    }
}