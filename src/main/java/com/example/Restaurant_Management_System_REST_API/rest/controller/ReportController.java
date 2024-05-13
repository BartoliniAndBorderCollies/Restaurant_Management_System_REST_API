package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //This will be done by manager and owner only

    //section for InventoryItem reports
    @GetMapping("/inventory/stockAmount/greaterThan")
    public List<InventoryItem> getInventoryItemByAmountGreaterThan(@RequestParam("amount") double amount) {
        return reportService.getInventoryItemByAmountGreaterThan(amount);
    }

    @GetMapping("/inventory/stockAmount/lessThan")
    public List<InventoryItem> getInventoryItemByAmountLessThan(@RequestParam("amount") double amount) {
        return reportService.getInventoryItemByAmountLessThan(amount);
    }

    @GetMapping("/inventory/stockAmount/findBySupplier")
    public List<InventoryItem> getInventoryItemBySupplierName(@RequestParam("supplier") String name) {
        return reportService.getInventoryItemBySupplierName(name);
    }

    //section for Customer reports
    @GetMapping("/customer/findByRole")
    public List<Customer> getCustomerByRole(@RequestParam("role") String roleName) {
        return reportService.getCustomerByRole(roleName);
    }

}
