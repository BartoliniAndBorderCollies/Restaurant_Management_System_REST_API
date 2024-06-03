package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderMenuRecordDTO.RestaurantOrderMenuRecordDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableForReportDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.service.ReportService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //This part is useful for staff (waitress, kitchen staff, manager and owner) and is covered with spring security
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "info/inventory/stockAmount/greaterThan", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    //MIME binary data type
    public ResponseEntity<StreamingResponseBody> getInventoryItemByAmountGreaterThan(@RequestParam("amount") double amount) {
        List<InventoryItem> items = reportService.getInventoryItemByAmountGreaterThan(amount);

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
        //Because in headers I didn't have a Content-Disposition I got response a zip file with html and xml files.
        //When I add below I manually add the header Content-Disposition. In this case I got a response as report xls file
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping("info/inventory/stockAmount/lessThan")
    public List<InventoryItem> getInventoryItemByAmountLessThan(@RequestParam("amount") double amount) {
        return reportService.getInventoryItemByAmountLessThan(amount);
    }

    @GetMapping("info/inventory/stockAmount/findBySupplier")
    public List<InventoryItem> getInventoryItemBySupplierName(@RequestParam("supplier") String name) {
        return reportService.getInventoryItemBySupplierName(name);
    }

    @GetMapping("info/customer/findWithReservation")
    public List<Customer> getCustomerWithReservation() {
        return reportService.getCustomerWithReservation();
    }

    @GetMapping("info/menuRecord/findAvailable")
    List<MenuRecord> getAvailableMenuRecords() {
        return reportService.getAvailableMenuRecords();
    }

    @GetMapping("info/menuRecord/findByCategory")
    public List<MenuRecord> getMenuRecordsByCategory(@RequestParam("category") Category category) {
        return reportService.getMenuRecordsByCategory(category);
    }

    @GetMapping("info/reservation/findByName")
    public List<Reservation> getReservationByName(@RequestParam("name") String name) {
        return reportService.getReservationByName(name);
    }

    @GetMapping("info/reservation/findByPeopleAmount/GreaterThan")
    public List<Reservation> getReservationByPeopleAmountGreaterThan(@RequestParam("amount") int peopleAmount) {
        return reportService.getReservationByPeopleAmountGreaterThan(peopleAmount);
    }

    @GetMapping("info/reservation/findByPeopleAmount/LessThan")
    public List<Reservation> getReservationByPeopleAmountLessThan(@RequestParam("amount") int peopleAmount) {
        return reportService.getReservationByPeopleAmountLessThan(peopleAmount);
    }

    @GetMapping("info/reservation/findByDate")
    public List<Reservation> getReservationByDateTimeAndAfter(@RequestParam("date_from") LocalDateTime dateTime) {
        return reportService.getReservationByDateTimeAndAfter(dateTime);
    }

    @GetMapping("info/reservation/findByCustomer")
    public List<Reservation> getReservationByCustomerName(@RequestParam("name") String name) {
        return reportService.getReservationByCustomerName(name);
    }

    @GetMapping("info/reservation/findByTable")
    public List<Reservation> getReservationByTable(@RequestParam("id") Long id) {
        return reportService.getReservationByTable(id);
    }

    @GetMapping("info/reservation/findByCustomerNameAndStartTime")
    public List<Reservation> getReservationByCustomerNameAndByStartTime(@RequestParam("name") String name,
                                                                        @RequestParam("date_from") LocalDateTime dateTimeFrom) {
        return reportService.getReservationByCustomerNameAndByStartTime(name, dateTimeFrom);
    }

    @GetMapping(value = "info/restaurantOrder/findByOrderTimeRange", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getRestaurantOrderByOrderTimeRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<RestaurantOrder> restaurantOrderByOrderTimeRange = reportService.getRestaurantOrderByOrderTimeRange(startDate, endDate);

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("restaurantOrdersByTimeRange");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping("info/restaurantOrder/findByOrderStatus")
    public List<RestaurantOrder> getRestaurantOrderByOrderStatus(@RequestParam("order_status") OrderStatus orderStatus) {
        return reportService.getRestaurantOrderByOrderStatus(orderStatus);
    }

    @GetMapping("info/restaurantOrder/findByTable")
    public List<RestaurantOrder> getRestaurantOrderByTable(@RequestParam("table_id") Long id) {
        return reportService.getRestaurantOrderByTable(id);
    }

    @GetMapping("info/restaurantOrder/findByTableAndTimePeriod")
    public List<RestaurantOrder> getRestaurantOrderByTableAndTimeRange(@RequestParam("table_id") Long id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getRestaurantOrderByTableAndTimeRange(id, startDate, endDate);
    }

    @GetMapping("info/restaurantOrder/findByTotalAmountToPayRange")
    public List<RestaurantOrder> getRestaurantOrderByTotalAmountToPayRange(@RequestParam("amount_from") double amountFrom,
                                                                           @RequestParam("amount_to") double amountTo) {
        return reportService.getRestaurantOrderByTotalAmountToPayRange(amountFrom, amountTo);
    }

    @GetMapping("info/restaurantOrder/getMenuRecordsFromRestaurantOrderId")
    public RestaurantOrderMenuRecordDTO getMenuRecordsFromRestaurantOrderId(@RequestParam("restaurantOrder_id") Long id) throws NotFoundInDatabaseException {
        return reportService.getMenuRecordsFromRestaurantOrderId(id);
    }

    @GetMapping("info/supplier/findByName")
    public List<Supplier> getSupplierByName(@RequestParam("name") String name) {
        return reportService.getSupplierByName(name);
    }

    @GetMapping("info/supplier/findByCity")
    public List<Supplier> getSupplierByCity(@RequestParam("city") String cityName) {
        return reportService.getSupplierByCity(cityName);
    }

    @GetMapping("info/table/findById")
    public TableForReportDTO getTableById(@RequestParam("id") Long id) throws NotFoundInDatabaseException {
        return reportService.getTableById(id);
    }

    @GetMapping("info/table/findByAvailability")
    public List<Table> getTableByAvailability(@RequestParam("available") boolean isAvailable) {
        return reportService.getTableByAvailability(isAvailable);
    }

    //This part is restricted for manager and owner only! and of course it is covered with spring security
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping("report/customer/findByRole")
    public List<Customer> getCustomerByRole(@RequestParam("role") String roleName) {
        return reportService.getCustomerByRole(roleName);
    }

    @GetMapping("report/restaurantOrder/findTotalSumInPeriodTime")
    public double getTotalSumRestaurantOrdersInPeriodTime(@RequestParam("time_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeFrom,
                                                          @RequestParam("time_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeTo) {
        return reportService.getTotalSumRestaurantOrdersInPeriodTime(timeFrom, timeTo);
    }
}
