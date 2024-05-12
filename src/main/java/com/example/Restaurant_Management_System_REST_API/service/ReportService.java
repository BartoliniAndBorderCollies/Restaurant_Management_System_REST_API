package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {

    private final InventoryItemRepository inventoryItemRepository;

    public List<InventoryItem> getInventoryItemByAmountGreaterThan(double amount) {
        return inventoryItemRepository.findByAmountGreaterThan(amount);
    }

    public List<InventoryItem> getInventoryItemByAmountLessThan(double amount) {
        return inventoryItemRepository.findByAmountLessThan(amount);
    }
}
