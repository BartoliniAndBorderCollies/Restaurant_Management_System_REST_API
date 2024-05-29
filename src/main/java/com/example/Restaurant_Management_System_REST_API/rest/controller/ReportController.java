package com.example.Restaurant_Management_System_REST_API.rest.controller;

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
import java.util.List;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;

    //This part is useful for staff (waitress, kitchen staff, manager and owner) and is covered with spring security
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "/inventory/stockAmount/greaterThan", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getInventoryItemByAmountGreaterThan(@RequestParam("amount") double amount) {
        List<InventoryItem> items = reportService.getInventoryItemByAmountGreaterThan(amount);

        StreamingResponseBody stream = outputStream -> {
            Workbook workbook = new XSSFWorkbook();
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
            workbook.close();
        };
        //Because in headers I didn't have a Content-Disposition I got response a zip file with html and xml files.
        //When I add below I manually add the header Content-Disposition. In this case I got a response as report xls file
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=report.xlsx");

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping("/inventory/stockAmount/lessThan")
    public List<InventoryItem> getInventoryItemByAmountLessThan(@RequestParam("amount") double amount) {
        return reportService.getInventoryItemByAmountLessThan(amount);
    }

    @GetMapping("/inventory/stockAmount/findBySupplier")
    public List<InventoryItem> getInventoryItemBySupplierName(@RequestParam("supplier") String name) {
        return reportService.getInventoryItemBySupplierName(name);
    }

    @GetMapping("/customer/findWithReservation")
    public List<Customer> getCustomerWithReservation() {
        return reportService.getCustomerWithReservation();
    }

    @GetMapping("/menuRecord/findAvailable")
    List<MenuRecord> getAvailableMenuRecords() {
        return reportService.getAvailableMenuRecords();
    }

    @GetMapping("/menuRecord/findByCategory")
    public List<MenuRecord> getMenuRecordsByCategory(@RequestParam("category") Category category) {
        return reportService.getMenuRecordsByCategory(category);
    }

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

    @GetMapping("/reservation/findByCustomerNameAndStartTime")
    public List<Reservation> getReservationByCustomerNameAndByStartTime(@RequestParam("name") String name,
                                                                       @RequestParam("date_from") LocalDateTime dateTimeFrom) {
        return reportService.getReservationByCustomerNameAndByStartTime(name, dateTimeFrom);
    }

    @GetMapping("/restaurantOrder/findByOrderTimeRange")
    public List<RestaurantOrder> getRestaurantOrderByOrderTimeRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getRestaurantOrderByOrderTimeRange(startDate, endDate);
    }

    @GetMapping("/restaurantOrder/findByOrderStatus")
    public List<RestaurantOrder> getRestaurantOrderByOrderStatus(@RequestParam("order_status") OrderStatus orderStatus) {
        return reportService.getRestaurantOrderByOrderStatus(orderStatus);
    }

    @GetMapping("/restaurantOrder/findByTable")
    public List<RestaurantOrder> getRestaurantOrderByTable(@RequestParam("table_id") Long id) {
        return reportService.getRestaurantOrderByTable(id);
    }

    @GetMapping("/restaurantOrder/findByTableAndTimePeriod")
    public List<RestaurantOrder> getRestaurantOrderByTableAndTimeRange(@RequestParam("table_id") Long id,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return reportService.getRestaurantOrderByTableAndTimeRange(id, startDate, endDate);
    }

    @GetMapping("/restaurantOrder/findByTotalAmountToPayRange")
    public List<RestaurantOrder> getRestaurantOrderByTotalAmountToPayRange(@RequestParam("amount_from") double amountFrom,
                                                                           @RequestParam("amount_to") double amountTo) {
        return reportService.getRestaurantOrderByTotalAmountToPayRange(amountFrom, amountTo);
    }

    @GetMapping("/supplier/findByName")
    public List<Supplier> getSupplierByName(@RequestParam("name") String name) {
        return reportService.getSupplierByName(name);
    }

    @GetMapping("/supplier/findByCity")
    public List<Supplier> getSupplierByCity(@RequestParam("city") String cityName) {
        return reportService.getSupplierByCity(cityName);
    }

    @GetMapping("/table/findById")
    public TableForReportDTO getTableById(@RequestParam("id") Long id) throws NotFoundInDatabaseException {
        return reportService.getTableById(id);
    }


    //findTableByAvailability



    //This part is restricted for manager and owner only! and of course is covered with spring security
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping("/customer/findByRole")
    public List<Customer> getCustomerByRole(@RequestParam("role") String roleName) {
        return reportService.getCustomerByRole(roleName);
    }


    //amount of money for sales in period of time


    //TODO: add spring security coverage for this module
}
