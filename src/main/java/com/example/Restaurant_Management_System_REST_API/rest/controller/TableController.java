package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.TableService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/table")
@AllArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/add")
    public TableDTO add(@RequestBody TableDTO tableDTO) {
        return tableService.add(tableDTO);
    }

    @GetMapping("/findAll")
    public List<TableDTO> findAll() {
        return tableService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseDTO deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return tableService.deleteById(id);
    }
}
