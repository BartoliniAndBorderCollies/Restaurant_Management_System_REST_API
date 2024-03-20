package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final ModelMapper modelMapper;

    public Table add(Table table) {
        return tableRepository.save(table);
    }

    public List<Table> findAll() {
        List<Table> tableList = new ArrayList<>();
        tableRepository.findAll().forEach(table -> tableList.add(modelMapper.map(table, Table.class)));

        return tableList;
    }
}
