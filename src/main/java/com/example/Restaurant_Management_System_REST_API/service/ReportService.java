package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderMenuRecordDTO.RestaurantOrderMenuRecordDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableForReportDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.*;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public StreamingResponseBody getInventoryItemByAmountGreaterThan(double amount) {

        List<InventoryItem> items = inventoryItemRepository.findByAmountGreaterThan(amount);

        // The StreamingResponseBody is used to stream the response back to the client. This is particularly useful for large files which can’t be held in memory.

        //Below I create an excel file using Apache POI library and then I write it to outputStream for the client to download it
        StreamingResponseBody stream = outputStream -> {
            Workbook workbook = new XSSFWorkbook(); // a top level object to create sheets and other operations
            Sheet sheet = workbook.createSheet("InventoryItems");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Price");
            headerRow.createCell(4).setCellValue("Amount");
            headerRow.createCell(5).setCellValue("Supplier Name");
            headerRow.createCell(6).setCellValue("Supplier Street");
            headerRow.createCell(7).setCellValue("Supplier House Number");
            headerRow.createCell(8).setCellValue("Supplier City");
            headerRow.createCell(9).setCellValue("Supplier Postal Code");
            headerRow.createCell(10).setCellValue("Supplier Telephone Number");

            // Fill data rows
            for (int i = 0; i < items.size(); i++) {
                InventoryItem item = items.get(i);
                Supplier supplier = item.getSupplier();
                ContactDetails contactDetails = supplier.getContactDetails();
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getName());
                row.createCell(2).setCellValue(item.getDescription());
                row.createCell(3).setCellValue(item.getPrice());
                row.createCell(4).setCellValue(item.getAmount());
                row.createCell(5).setCellValue(contactDetails.getName());
                row.createCell(6).setCellValue(contactDetails.getStreet());
                row.createCell(7).setCellValue(contactDetails.getHouseNumber());
                row.createCell(8).setCellValue(contactDetails.getCity());
                row.createCell(9).setCellValue(contactDetails.getPostalCode());
                row.createCell(10).setCellValue(contactDetails.getTelephoneNumber());
            }

            workbook.write(outputStream);
            workbook.close();//I close cause I want to free memory resources, save the input data,
        };

        return stream;

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

    public StreamingResponseBody getRestaurantOrderByOrderTimeRange(LocalDate startDate, LocalDate endDate) {

        List<RestaurantOrder> restaurantOrderByOrderTimeRange = restaurantOrderRepository.findByOrderTimeRange(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("restaurantOrdersByTimeRange");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Restaurant Order ID");
            headerRow.createCell(1).setCellValue("Order time");
            headerRow.createCell(2).setCellValue("Order status");
            headerRow.createCell(3).setCellValue("Table id");
            headerRow.createCell(4).setCellValue("Telephone number");
            headerRow.createCell(5).setCellValue("Total amount to pay");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Fill data rows
            for (int i = 0; i < restaurantOrderByOrderTimeRange.size(); i++) {
                RestaurantOrder restaurantOrder = restaurantOrderByOrderTimeRange.get(i);
                OrderStatus orderStatus = restaurantOrder.getOrderStatus();

                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(restaurantOrder.getId());
                row.createCell(1).setCellValue(restaurantOrder.getOrderTime().format(formatter));
                row.createCell(2).setCellValue(orderStatus.toString());
                row.createCell(3).setCellValue(restaurantOrder.getTable().getId());
                row.createCell(4).setCellValue(restaurantOrder.getTelephoneNumber());
                row.createCell(5).setCellValue(restaurantOrder.getTotalAmountToPay());

            }
            workbook.write(outputStream);
            workbook.close();
        };

        return stream;
    }

    public StreamingResponseBody getRestaurantOrderByOrderStatus(OrderStatus orderStatus) {

        List<RestaurantOrder> restaurantOrderByOrderStatus = restaurantOrderRepository.findByOrderStatus(orderStatus);

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("restaurantOrdersByOrderStatus");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Restaurant Order ID");
            headerRow.createCell(1).setCellValue("Order time");
            headerRow.createCell(2).setCellValue("Order status");
            headerRow.createCell(3).setCellValue("Table id");
            headerRow.createCell(4).setCellValue("Telephone number");
            headerRow.createCell(5).setCellValue("Total amount to pay");
            headerRow.createCell(6).setCellValue("Menu record name");
            headerRow.createCell(7).setCellValue("Portions amount");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            //fill the data
            int rowIndex = 1;
            for (int i = 0; i < restaurantOrderByOrderStatus.size(); i++) {
                RestaurantOrder restaurantOrder = restaurantOrderByOrderStatus.get(i);
                List<RestaurantOrderMenuRecord> restaurantOrders = restaurantOrder.getRestaurantOrders();

                for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord : restaurantOrders) {
                    MenuRecord menuRecord = eachRestaurantOrderMenuRecord.getMenuRecord();
                    Double portionsAmount = eachRestaurantOrderMenuRecord.getPortionsAmount();

                    Row row = sheet.createRow(rowIndex++);// this adds new row if restaurant order has multiple orders
                    row.createCell(0).setCellValue(restaurantOrder.getId());
                    row.createCell(1).setCellValue(restaurantOrder.getOrderTime().format(formatter));
                    row.createCell(2).setCellValue(restaurantOrder.getOrderStatus().toString());
                    row.createCell(3).setCellValue(restaurantOrder.getTable().getId());
                    row.createCell(4).setCellValue(restaurantOrder.getTelephoneNumber());
                    row.createCell(5).setCellValue(restaurantOrder.getTotalAmountToPay());
                    row.createCell(6).setCellValue(menuRecord.getName());
                    row.createCell(7).setCellValue(portionsAmount);
                }
            }
            workbook.write(outputStream);
            workbook.close();
        };

        return stream;
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
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(RestaurantOrder.class));

        // I fetch necessary data from repo. this is the list of RestaurantOrderMenuRecord given for specific RestaurantOrder
        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByRestaurantOrderId(id);

        //Now I need to loop over this fetched list. I take menuRecords from this list. Then I convert it to menuRecordForOrderDTO
        // and I set the portions amount and finally add it to the list of MenuRecordForOrderDTO
        for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord : restaurantOrderMenuRecordList) {
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

    public List<RestaurantOrderMenuRecord> getRestaurantOrderMenuRecordInTimePeriod(LocalDate timeFrom, LocalDate timeTo) {
        return restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByTimePeriod(timeFrom.atStartOfDay(),
                timeTo.plusDays(1).atStartOfDay());
    }

}
