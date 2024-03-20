package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/table")
@AllArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/add")
    public Table add(@RequestBody Table table) {
        return tableService.add(table);
    }

    @GetMapping("/findAll")
    public List<Table> findAll() {
        return tableService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return tableService.deleteById(id);
    }
}
