package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
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

    @GetMapping("/customer/findWithReservation")
    public List<Customer> getCustomerWithReservation() {
        return reportService.getCustomerWithReservation();
    }

    //section for MenuRecord reports
    @GetMapping("/menuRecord/findAvailable")
    List<MenuRecord> getAvailableMenuRecords() {
        return reportService.getAvailableMenuRecords();
    }

    @GetMapping("/menuRecord/findByCategory")
    public List<MenuRecord> getMenuRecordsByCategory(@RequestParam("category")Category category) {
        return reportService.getMenuRecordsByCategory(category);
    }

    //section for Reservation reports
    @GetMapping("/reservation/findByName")
    public List<Reservation> getReservationByName(@RequestParam("name") String name) {
        return reportService.getReservationByName(name);
    }

    //findByPeopleAmountGreaterThan
    //findByPeopleAmountLessThan

    //findByDate
    //findByCustomer
    //findByTables
    //findByCustomerAndDate


}
