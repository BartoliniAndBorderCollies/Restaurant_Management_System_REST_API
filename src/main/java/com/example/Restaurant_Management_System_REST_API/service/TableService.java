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

    private Table findById(Long id) throws NotFoundInDatabaseException {
        return tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));
    }

    public ResponseEntity<?> deleteById(Long id) throws NotFoundInDatabaseException {
        Table tableToDelete = tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));

        tableRepository.delete(tableToDelete);

        return new ResponseEntity<>("Table with id " + tableToDelete.getId() + " has been deleted!", HttpStatus.OK);
    }

    void iterateAndSetTablesToReservationAndSave(Reservation reservation) throws NotFoundInDatabaseException {
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

    void iterateAndSetReservationToNullInTablesAndSave(Reservation reservationToBeDeleted) {
        for (Table table : reservationToBeDeleted.getTables()) {
            table.setReservation(null);
            table.setAvailable(true);
            tableRepository.save(table);
        }
    }

    void checkIfTablesAreAvailable(Reservation reservation) throws NotFoundInDatabaseException {
        for (Table table : reservation.getTables()) {
            Table existingTable = findById(table.getId());

            if (existingTable != null) {
                for (Reservation existingReservation : existingTable.getReservationList()) {
                    if (isTimeConflict(existingReservation.getStart(), reservation.getStart())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Table with id " + table.getId() +
                                " is not available at the requested time.");
                    }
                }
            }
        }
    }

    boolean isTimeConflict(LocalDateTime existingStart, LocalDateTime newStart) {
        LocalDateTime existingEnd = existingStart.plusHours(2);
        LocalDateTime newEnd = newStart.plusHours(2);

        return (newStart.isEqual(existingStart) || newStart.isAfter(existingStart)) && newStart.isBefore(existingEnd)
                || (newEnd.isAfter(existingStart) && newEnd.isBefore(existingEnd))
                || (existingStart.isAfter(newStart) && existingStart.isBefore(newEnd));
    }
}
