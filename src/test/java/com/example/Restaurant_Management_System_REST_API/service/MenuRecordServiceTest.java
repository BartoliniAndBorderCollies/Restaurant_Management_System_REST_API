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
    private MenuRecordDTORequest menuRecordDTORequest;
    private MenuRecord menuRecord;
    private MenuRecordDTOResponse menuRecordDTOResponse;

    @BeforeEach
    public void setUp() {
        Set<String> ingredients = new HashSet<>();
        ingredients.add("water");
        ingredients.add("hops");
        ingredients.add("barley");

        menuRecordDTORequest = new MenuRecordDTORequest(1L, ingredients, Category.BEVERAGE, true);
        menuRecord = new MenuRecord(1L, ingredients, Category.BEVERAGE, true);
        menuRecordDTOResponse = new MenuRecordDTOResponse(1L, ingredients, Category.BEVERAGE, false);

        menuRecordRepository = mock(MenuRecordRepository.class);
        modelMapper = mock(ModelMapper.class);

        menuRecordService = new MenuRecordService(menuRecordRepository, modelMapper);
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

}