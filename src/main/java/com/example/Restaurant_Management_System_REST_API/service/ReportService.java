package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderMenuRecordDTO.RestaurantOrderMenuRecordDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableForReportDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final TableRepository tableRepository;
    private final ModelMapper modelMapper;
    private final RestaurantOrderMenuRecordRepository restaurantOrderMenuRecordRepository;

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

    public List<Supplier> getSupplierByCity(String cityName) {
        return supplierRepository.findByContactDetails_City(cityName);
    }

    public TableForReportDTO getTableById(Long id) throws NotFoundInDatabaseException {
        Table table = tableRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(Table.class));

        return modelMapper.map(table, TableForReportDTO.class);
    }

    public List<Table> getTableByAvailability(boolean isAvailable) {
        return tableRepository.findByIsAvailable(isAvailable);
    }

    public double getTotalSumRestaurantOrdersInPeriodTime(LocalDate timeFrom, LocalDate timeTo) {
        double totalSpent = 0;

        List<RestaurantOrder> allRestaurantOrdersInSpecificTimePeriod = restaurantOrderRepository.findByTimeRange(timeFrom.atStartOfDay(),
                timeTo.plusDays(1).atStartOfDay());

        for (RestaurantOrder restaurantOrder : allRestaurantOrdersInSpecificTimePeriod) {
            double totalAmountToPayPerRestaurantOrder = restaurantOrder.getTotalAmountToPay();
            totalSpent += totalAmountToPayPerRestaurantOrder;
        }
        return totalSpent;
    }

    public RestaurantOrderMenuRecordDTO getMenuRecordsFromRestaurantOrderId(Long id) throws NotFoundInDatabaseException {
        List<MenuRecordForOrderDTO> menuRecordForOrderDTOList = new ArrayList<>();

        //I use here object RestaurantOrder taken straight from repo because when I use service I got object without portions amount
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(()-> new NotFoundInDatabaseException(RestaurantOrder.class));

        // I fetch necessary data from repo. this is the list of RestaurantOrderMenuRecord given for specific RestaurantOrder
        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByRestaurantOrderId(id);

        //Now I need to loop over this fetched list. I take menuRecords from this list. Then I convert it to menuRecordForOrderDTO
        // and I set the portions amount and finally add it to the list of MenuRecordForOrderDTO
        for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord: restaurantOrderMenuRecordList) {
            MenuRecord menuRecord = eachRestaurantOrderMenuRecord.getMenuRecord();
            MenuRecordForOrderDTO menuRecordForOrderDTO = modelMapper.map(menuRecord, MenuRecordForOrderDTO.class);
            menuRecordForOrderDTO.setPortionsAmount(eachRestaurantOrderMenuRecord.getPortionsAmount());
            menuRecordForOrderDTOList.add(menuRecordForOrderDTO);
        }

        RestaurantOrderMenuRecordDTO restaurantOrderMenuRecordDTO = new RestaurantOrderMenuRecordDTO();
        restaurantOrderMenuRecordDTO.setMenuRecord(menuRecordForOrderDTOList);
        restaurantOrderMenuRecordDTO.setTotalAmountToPay(restaurantOrder.getTotalAmountToPay());


        return restaurantOrderMenuRecordDTO;
    }


}
