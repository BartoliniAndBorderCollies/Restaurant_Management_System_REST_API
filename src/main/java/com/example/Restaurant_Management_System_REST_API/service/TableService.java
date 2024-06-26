package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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

    public ResponseDTO deleteById(Long id) throws NotFoundInDatabaseException {
        Table tableToDelete = tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));

        tableRepository.delete(tableToDelete);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Table with id " + tableToDelete.getId() + " has been deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }

    void iterateAndSetTablesToReservationAndSave(Reservation reservation) throws NotFoundInDatabaseException {
        for (Table table : reservation.getTables()) {
            Table takenTable = checkIfTableExist(table.getId());

            takenTable.getReservationList().add(reservation);

            table.setAvailable(false);
            tableRepository.save(table);
        }
    }

    Table checkIfTableExist(Long id) throws NotFoundInDatabaseException {
        return tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));
    }

    void iterateAndSetReservationToNullInTablesAndSave(Reservation reservationToBeDeleted) {
        for (Table table : reservationToBeDeleted.getTables()) {
            table.setReservationList(null);
            table.setAvailable(true);
            tableRepository.save(table);
        }
    }

    void checkIfTablesAreAvailable(Reservation reservation) throws NotFoundInDatabaseException {
        for (Table table : reservation.getTables()) {
            checkTableAvailability(table, reservation);
        }
    }

    private void checkTableAvailability(Table table, Reservation reservation) throws NotFoundInDatabaseException {
        Table existingTable = checkIfTableExist(table.getId());

        checkReservationConflict(existingTable, reservation);
    }

    private void checkReservationConflict(Table table, Reservation reservation) {
        for (Reservation existingReservation : table.getReservationList()) {
            if (isTimeConflict(existingReservation.getStart(), reservation.getStart())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Table with id " + table.getId() +
                        " is not available at the requested time.");
            }
        }
    }

    boolean isTimeConflict(LocalDateTime existingStart, LocalDateTime newStart) {
        LocalDateTime existingEnd = existingStart.plusHours(2);
        LocalDateTime newEnd = newStart.plusHours(2);

        //Time conflict is true when new and old reservation are at the same date, same hours (plus/minus 2 hours excluding), for example:
        // reservation one: 18:00 today, reservation two: 16:30 - conflict, true
        // reservation one: 18:00 today, reservation two: 19:59 - conflict, true
        // reservation one: 18:00 today, reservation two: 20:00 - NO conflict, false
        // reservation one: 18:00 today, reservation two: 16:30 - conflict, true
        // reservation one: 18:00 today, reservation two: 16:01 - conflict, true
        // reservation one: 18:00 today, reservation two: 16:00 - NO conflict, false

        return (newStart.isEqual(existingStart) || newStart.isAfter(existingStart)) && newStart.isBefore(existingEnd)
                || (newEnd.isAfter(existingStart) && newEnd.isBefore(existingEnd))
                || (existingStart.isAfter(newStart) && existingStart.isBefore(newEnd));
    }
}
