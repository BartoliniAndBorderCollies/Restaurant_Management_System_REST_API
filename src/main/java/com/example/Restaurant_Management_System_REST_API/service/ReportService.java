package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {

    private final InventoryItemRepository inventoryItemRepository;
    private final CustomerRepository customerRepository;

    //section for InventoryItem reports
    public List<InventoryItem> getInventoryItemByAmountGreaterThan(double amount) {
        return inventoryItemRepository.findByAmountGreaterThan(amount);
    }

    public List<InventoryItem> getInventoryItemByAmountLessThan(double amount) {
        return inventoryItemRepository.findByAmountLessThan(amount);
    }

    public List<InventoryItem> getInventoryItemBySupplierName(String name) {
        return inventoryItemRepository.findBySupplier_ContactDetails_Name(name);
    }

    //section for Customer reports
    public List<Customer> getCustomerByRole(String roleName) {
        return customerRepository.findByAuthorities_Authority_name(roleName);
    }

    public List<Customer> getCustomerWithReservation() {
        return customerRepository.findByReservation();
    }

}
