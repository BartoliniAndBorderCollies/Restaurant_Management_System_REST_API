package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TableServiceTest {

    private ModelMapper modelMapper;
    private TableRepository tableRepository;
    private TableService tableService;
    private TableDTO tableDTO;
    private Table table;
    private Reservation reservation;

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

    @BeforeEach
    public void prepareReservationMock() {
        reservation = mock(Reservation.class);
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

    @Test
    public void deleteById_ShouldThrowNotFoundInDatabaseException_WhenTableNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> tableService.deleteById(nonExistedId));
    }

    @Test
    public void deleteById_ShouldCallOnTableRepoExactlyOnce_WhenTableIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        Table tableToDelete = new Table(1L, true, null, null);
        when(tableRepository.findById(tableToDelete.getId())).thenReturn(Optional.of(tableToDelete));

        //Act
        tableService.deleteById(tableToDelete.getId());
        //Assert
        verify(tableRepository, times(1)).delete(tableToDelete);
    }

    @Test
    public void deleteById_ShouldInteractWithDependenciesCorrectlyAndReturnResponseEntity_WhenTableIdIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Table tableToDelete = new Table(1L, true, null, null);
        when(tableRepository.findById(tableToDelete.getId())).thenReturn(Optional.of(tableToDelete));

        //Act
        ResponseEntity<?> actualResponse = tableService.deleteById(tableToDelete.getId());

        //Assert
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals("Table with id " + tableToDelete.getId() + " has been deleted!", actualResponse.getBody());
    }

    @Test
    public void iterateAndSetTablesToReservationAndSave_ShouldThrowNotFoundInDbException_WhenTableNotExist() {
        //Arrange
        List<Table> tableList = Arrays.asList(table);
        when(reservation.getTables()).thenReturn(tableList);


        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> tableService.iterateAndSetTablesToReservationAndSave(reservation));
    }

    @Test
    public void iterateAndSetTablesToReservationAndSave_ShouldCallSaveOnTableRepoAndSetTablesCorrectly_WhenReservationIsGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Reservation reservation = mock(Reservation.class);
        Table table1 = mock(Table.class);
        Table table2 = mock(Table.class);
        List<Table> tableList = Arrays.asList(table1, table2);

        when(reservation.getTables()).thenReturn(tableList);
        when(table1.getReservationList()).thenReturn(new ArrayList<>());
        when(table2.getReservationList()).thenReturn(new ArrayList<>());
        when(tableRepository.findById(table1.getId())).thenReturn(Optional.of(table1));
        when(tableRepository.findById(table2.getId())).thenReturn(Optional.of(table2));

        //Act
        tableService.iterateAndSetTablesToReservationAndSave(reservation);

        //Assert

        // Verify that the save method of the tableRepository mock is called exactly twice, because there are two tables in the
        // reservation, and for each table, the save method should be called once after updating the table's reservation
        // list and availability.
        verify(tableRepository, times(2)).save(any(Table.class));

        // Verify that the setAvailable method of the table1 and table2 mock is called exactly once with the argument false.
        verify(table1).setAvailable(false);
        verify(table2).setAvailable(false);
    }


}