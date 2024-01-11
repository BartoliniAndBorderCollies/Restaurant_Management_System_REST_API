package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryItemService implements GenericBasicCrudOperations<InventoryItem, InventoryItem, Long>{
    @Override
    public InventoryItem create(InventoryItem item) {
        return null;
    }

    @Override
    public InventoryItem read(Long id) {
        return null;
    }

    @Override
    public List<?> readAll() {
        return null;
    }

    @Override
    public InventoryItem update(InventoryItem item) {
        return null;
    }

    @Override
    public void delete(InventoryItem item) {

    }
}
