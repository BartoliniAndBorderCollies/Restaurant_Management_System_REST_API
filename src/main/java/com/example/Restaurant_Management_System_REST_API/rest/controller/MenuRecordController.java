package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.service.MenuRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu/record")
@AllArgsConstructor
public class MenuRecordController {

    private MenuRecordService menuRecordService;

    @PostMapping("/add") // This will be done by only owner and manager
    public MenuRecord create(@RequestBody MenuRecord menuRecord) {
        return menuRecordService.create(menuRecord);
    }

    @GetMapping("/{id}") // this will be done by all roles
    public MenuRecord readById (@PathVariable Long id) throws NotFoundInDatabaseException {
        return menuRecordService.findById(id);
    }

    @GetMapping // this will be done by all roles
    public List<MenuRecord> readAll () {
        return menuRecordService.findAll();
    }

    @PutMapping("/update/{id}") // This will be done only by owner and manager
    public MenuRecord update(@PathVariable Long id, @RequestBody MenuRecord menuRecord) throws NotFoundInDatabaseException {
        return menuRecordService.update(id, menuRecord);
    }

    @DeleteMapping("/delete/{id}") // This will be done only by owner and manager
    public ResponseEntity<?> delete(@PathVariable Long id) throws NotFoundInDatabaseException {
        return menuRecordService.delete(id);
    }
}
