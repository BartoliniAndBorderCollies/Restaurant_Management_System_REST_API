package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
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

    //TODO: Junit tests, integrations tests. Merge to develop branch!

    @PostMapping("/add") // This will be done only by owner and manager
    public MenuRecordDTOResponse create(@RequestBody MenuRecordDTORequest menuRecordDTORequest) {
        return menuRecordService.create(menuRecordDTORequest);
    }

    @GetMapping("/{id}") // this will be done by all roles
    public MenuRecordDTOResponse readById (@PathVariable Long id) throws NotFoundInDatabaseException {
        return menuRecordService.findById(id);
    }

    @GetMapping // this will be done by all roles
    public List<MenuRecordDTOResponse> readAll () {
        return menuRecordService.findAll();
    }

    @PutMapping("/update/{id}") // This will be done only by owner and manager
    public MenuRecordDTOResponse update(@PathVariable Long id, @RequestBody MenuRecordDTORequest menuRecordDTORequest)
            throws NotFoundInDatabaseException {
        return menuRecordService.update(id, menuRecordDTORequest);
    }

    @DeleteMapping("/delete/{id}") // This will be done only by owner and manager
    public ResponseEntity<?> delete(@PathVariable Long id) throws NotFoundInDatabaseException {
        return menuRecordService.delete(id);
    }
}
