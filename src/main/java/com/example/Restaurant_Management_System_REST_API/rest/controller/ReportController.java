package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<MenuRecord> getMenuRecordsByCategory(@RequestParam("category") Category category) {
        return reportService.getMenuRecordsByCategory(category);
    }

    //section for Reservation reports
    @GetMapping("/reservation/findByName")
    public List<Reservation> getReservationByName(@RequestParam("name") String name) {
        return reportService.getReservationByName(name);
    }

    @GetMapping("/reservation/findByPeopleAmount/GreaterThan")
    public List<Reservation> getReservationByPeopleAmountGreaterThan(@RequestParam("amount") int peopleAmount) {
        return reportService.getReservationByPeopleAmountGreaterThan(peopleAmount);
    }

    @GetMapping("/reservation/findByPeopleAmount/LessThan")
    public List<Reservation> getReservationByPeopleAmountLessThan(@RequestParam("amount") int peopleAmount) {
        return reportService.getReservationByPeopleAmountLessThan(peopleAmount);
    }

    @GetMapping("/reservation/findByDate")
    public List<Reservation> getReservationByDateTimeAndAfter(@RequestParam("date_from") LocalDateTime dateTime) {
        return reportService.getReservationByDateTimeAndAfter(dateTime);
    }

    @GetMapping("/reservation/findByCustomer")
    public List<Reservation> getReservationByCustomerName(@RequestParam("name") String name) {
        return reportService.getReservationByCustomerName(name);
    }

    @GetMapping("/reservation/findByTable")
    public List<Reservation> getReservationByTable(@RequestParam("id") Long id) {
        return reportService.getReservationByTable(id);
    }

    @GetMapping("reservation/findByCustomerAndTimePeriod")
    public List<Reservation> getReservationByCustomerNameAndTimePeriod(@RequestParam("name") String name,
                                                                       @RequestParam("date_from") LocalDate dateFrom,
                                                                       @RequestParam("date_to") LocalDate dateTo) {
        return reportService.getReservationByCustomerNameAndTimePeriod(name, dateFrom, dateTo);
    }


}
