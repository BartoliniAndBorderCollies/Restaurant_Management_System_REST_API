package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService implements GenericBasicCrudOperations<Customer, Customer, Long> {

    @Override
    public Customer create(Customer customer) {
        return null;
    }

    @Override
    public Customer read(Long id) {
        return null;
    }

    @Override
    public List<?> readAll() {
        return null;
    }

    @Override
    public Customer update(Customer customer) {
        return null;
    }

    @Override
    public void delete(Customer customer) {

    }
}
