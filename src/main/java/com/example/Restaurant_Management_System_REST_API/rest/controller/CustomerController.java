package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {

    private CustomerService customerService;

    @PostMapping("/add") //this will be done only by owner and manager
    public CustomerDTOResponse create(@RequestBody CustomerDTORequest customerDTORequest) {
        return customerService.create(customerDTORequest);
    }

    @GetMapping("/find/{id}") // this will be done by owner, manager and staff
    public CustomerDTOResponse findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return customerService.findById(id);
    }

    @GetMapping("/findAll")
    public List<CustomerDTOResponse> findAll() {
        return customerService.findAll();
    }

    @DeleteMapping("/delete/{id}") // this will be done only by the owner and manager
    public ResponseEntity<?> deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return customerService.delete(id);
    }
}
