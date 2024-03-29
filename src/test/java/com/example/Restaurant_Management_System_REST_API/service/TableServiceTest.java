package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TableServiceTest {

    private ModelMapper modelMapper;
    private TableRepository tableRepository;
    private TableService tableService;
    private TableDTO tableDTO;
    private Table table;

    @BeforeEach
    public void prepareEnvironment() {
        modelMapper = mock(ModelMapper.class);
        tableRepository = mock(TableRepository.class);
        tableService = new TableService(tableRepository, modelMapper);
    }

    @BeforeEach
    public void prepareTableDTOAndTable() {
        tableDTO = new TableDTO(1L);
        table = mock(Table.class);
    }

    @Test
    public void add_ShouldMapAndSaveAndMapAgainAndReturn_WhenTableDTOIsGiven() {
        //Arrange
        when(modelMapper.map(tableDTO, Table.class)).thenReturn(table);
        when(tableRepository.save(table)).thenReturn(table);
        when(modelMapper.map(table, TableDTO.class)).thenReturn(tableDTO);

        //Act
        TableDTO actual = tableService.add(tableDTO);

        //Assert
        assertEquals(tableDTO, actual);
    }

    @Test
    public void findAll_ShouldMapEachTableToDTOAndReturnTableDTOList_WhenTableExist() {
        //Arrange
        List<TableDTO> tableList = Arrays.asList(tableDTO);
        List<Table> tables = Arrays.asList(table);

        when(tableRepository.findAll()).thenReturn(tables);
        when(modelMapper.map(table, TableDTO.class)).thenReturn(tableDTO);

        //Act
        List<TableDTO> actual = tableService.findAll();

        //Assert
        assertIterableEquals(tableList, actual);
    }

}