package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final ModelMapper modelMapper;

    public TableDTO add(TableDTO tableDTO) {
        Table table = modelMapper.map(tableDTO, Table.class);
        tableRepository.save(table);

        return modelMapper.map(table, TableDTO.class);
    }

    public List<TableDTO> findAll() {
        List<TableDTO> tableList = new ArrayList<>();
        tableRepository.findAll().forEach(table -> tableList.add(modelMapper.map(table, TableDTO.class)));

        return tableList;
    }

    public TableDTO findById(Long id) throws NotFoundInDatabaseException {
        Table table = tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));
        return modelMapper.map(table, TableDTO.class);
    }

    public ResponseEntity<?> deleteById(Long id) throws NotFoundInDatabaseException {
        Table tableToDelete = tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));

        tableRepository.delete(tableToDelete);

        return new ResponseEntity<>("Table with id " + tableToDelete.getId() + " has been deleted!", HttpStatus.OK);
    }

    void iterateAndSetTablesToReservation(Reservation reservation) throws NotFoundInDatabaseException {
        for (Table table : reservation.getTables()) {
            checkIfTableExist(table.getId());
            table.setReservation(reservation);
            table.setAvailable(false);
            tableRepository.save(table);
        }
    }

    private void checkIfTableExist(Long id) throws NotFoundInDatabaseException {
        tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));
    }

    void iterateAndSetTablesToNullInReservationToDelete(Reservation reservationToBeDeleted) {
        for (Table table : reservationToBeDeleted.getTables()) {
            table.setReservation(null);
            table.setAvailable(true);
            tableRepository.save(table);
        }
    }

    void checkIfTablesAreAvailable(Reservation reservation) throws NotFoundInDatabaseException {
        for (Table table : reservation.getTables()) {
            Reservation existingReservation = findById(table.getId()).getReservation();
            if (existingReservation != null) {
                LocalDateTime existingReservationStart = existingReservation.getStart();
                LocalDateTime newReservationStart = reservation.getStart();
                if (existingReservationStart.isBefore(newReservationStart) &&
                        existingReservationStart.plusHours(2).isAfter(newReservationStart)) { // I assume that max time is 2 hours

                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Table with id " + table.getId() +
                            " is not available at the requested time.");
                }
            }
        }
    }
}
