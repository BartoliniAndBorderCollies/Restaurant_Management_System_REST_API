package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/add") //this will be done only by owner and manager
    public CustomerDTOResponse create(@RequestBody CustomerDTORequest customerDTORequest) {
        return customerService.create(customerDTORequest);
    }

    @GetMapping("/find/{id}") // this will be done by owner, manager and staff
    public CustomerDTOResponse findById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return customerService.findById(id);
    }

    @GetMapping("/findAll") // this will be done by owner, manager and staff
    public List<CustomerDTOResponse> findAll() {
        return customerService.findAll();
    }

    @PutMapping("/update/{id}") //this will be done only by the owner and manager
    public CustomerDTOResponse update(@PathVariable Long id, @RequestBody CustomerDTORequest customerDTORequest)
            throws NotFoundInDatabaseException {
        return customerService.update(id, customerDTORequest);
    }

    @DeleteMapping("/delete/{id}") // this will be done only by the owner and manager
    public ResponseDTO deleteById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return customerService.delete(id);
    }
}
