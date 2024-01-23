package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MenuRecordServiceTest {

    private MenuRecordService menuRecordService;
    private MenuRecordRepository menuRecordRepository;
    private ModelMapper modelMapper;
    private Set<String> ingredients;
    private MenuRecordDTORequest menuRecordDTORequest;

    @BeforeEach
    public void setUp() {
        ingredients = new HashSet<>();
        ingredients.add("water");
        ingredients.add("hops");
        ingredients.add("barley");

        menuRecordDTORequest = new MenuRecordDTORequest(1L, ingredients, Category.BEVERAGE, true);

        menuRecordRepository = mock(MenuRecordRepository.class);
        modelMapper = mock(ModelMapper.class);

        menuRecordService = new MenuRecordService(menuRecordRepository, modelMapper);
    }

    @Test
    public void create_ShouldInteractWithDependenciesCorrectly_WhenMenuRecordDTORequestIsGiven() {
        //Arrange
        MenuRecord menuRecord = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);
        MenuRecordDTOResponse expected = new MenuRecordDTOResponse(1L, ingredients, Category.BEVERAGE, false);

        when(modelMapper.map(menuRecordDTORequest, MenuRecord.class)).thenReturn(menuRecord);
        when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);
        when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(expected);

        //Act
        MenuRecordDTOResponse actual = menuRecordService.create(menuRecordDTORequest);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void create_ShouldReturnTypeMenuRecordDTOResponse_WhenMenuRecordDTORequestIsGiven() {
        //Arrange
        MenuRecordDTOResponse expected = new MenuRecordDTOResponse();
        MenuRecord menuRecord = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);

        when(modelMapper.map(menuRecordDTORequest, MenuRecord.class)).thenReturn(menuRecord);
        when(menuRecordRepository.save(menuRecord)).thenReturn(menuRecord);
        when(modelMapper.map(menuRecord, MenuRecordDTOResponse.class)).thenReturn(expected);

        //Act
        MenuRecordDTOResponse actual = menuRecordService.create(menuRecordDTORequest);

        //Assert
        assertEquals(expected, actual);
    }

}