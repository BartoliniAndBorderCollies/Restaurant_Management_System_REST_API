package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.TableDTOs.TableDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<?> deleteById(Long id) throws NotFoundInDatabaseException {
        Table tableToDelete = tableRepository.findById(id).orElseThrow(()-> new NotFoundInDatabaseException(Table.class));

        tableRepository.delete(tableToDelete);

        return new ResponseEntity<>("Table with id " + tableToDelete.getId() + " has been deleted!", HttpStatus.OK);
    }
}
