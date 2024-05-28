package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {

    private final InventoryItemRepository inventoryItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuRecordRepository menuRecordRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final SupplierRepository supplierRepository;

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

    public List<MenuRecord> getAvailableMenuRecords() {
        return menuRecordRepository.findByIsAvailable(true);
    }

    public List<MenuRecord> getMenuRecordsByCategory(Category category) {
        return menuRecordRepository.findByCategory(category);
    }

    public List<Reservation> getReservationByName(String name) {
        return reservationRepository.findByName(name);
    }

    public List<Reservation> getReservationByPeopleAmountGreaterThan(int peopleAmount) {
        return reservationRepository.findByPeopleAmountGreaterThan(peopleAmount);
    }

    public List<Reservation> getReservationByPeopleAmountLessThan(int peopleAmount) {
        return reservationRepository.findByPeopleAmountLessThan(peopleAmount);
    }

    public List<Reservation> getReservationByDateTimeAndAfter(LocalDateTime dateTime) {
        return reservationRepository.findByStartAndAfter(dateTime);
    }

    public List<Reservation> getReservationByCustomerName(String name) {
        return reservationRepository.findByCustomer_ContactDetails_Name(name);
    }

    public List<Reservation> getReservationByTable(Long id) {
        return reservationRepository.findByTables_Id(id);
    }

    public List<Reservation> getReservationByCustomerNameAndByStartTime(String name, LocalDateTime dateTimeFrom) {
        return reservationRepository.findByCustomer_ContactDetails_NameAndByStart(name, dateTimeFrom);
    }

    public List<RestaurantOrder> getRestaurantOrderByOrderTimeRange(LocalDate startDate, LocalDate endDate) {
        return restaurantOrderRepository.findByOrderTimeRange(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public List<RestaurantOrder> getRestaurantOrderByOrderStatus(OrderStatus orderStatus) {
        return restaurantOrderRepository.findByOrderStatus(orderStatus);
    }

    public List<RestaurantOrder> getRestaurantOrderByTable(Long id) {
        return restaurantOrderRepository.findByTableId(id);
    }

    public List<RestaurantOrder> getRestaurantOrderByTableAndTimeRange(Long id, LocalDate startDate, LocalDate endDate) {
        return restaurantOrderRepository.findByTableIdAndOrderTimeRange(id, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public List<RestaurantOrder> getRestaurantOrderByTotalAmountToPayRange(double amountFrom, double amountTo) {
        return restaurantOrderRepository.findByTotalAmountToPayRange(amountFrom, amountTo);
    }

    public List<Supplier> getSupplierByName(String name) {
        return supplierRepository.findByContactDetails_Name(name);
    }


}
