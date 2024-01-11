package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements GenericBasicCrudOperations<Order, Order, Long> {
    @Override
    public Order create(Order order) {
        return null;
    }

    @Override
    public Order read(Long id) {
        return null;
    }

    @Override
    public List<?> readAll() {
        return null;
    }

    @Override
    public Order update(Order order) {
        return null;
    }

    @Override
    public void delete(Order order) {

    }
}
