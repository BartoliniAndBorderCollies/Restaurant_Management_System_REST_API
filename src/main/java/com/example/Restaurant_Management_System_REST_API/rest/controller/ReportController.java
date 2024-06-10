package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderMenuRecordDTO.RestaurantOrderMenuRecordDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableForReportDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.service.ReportService;
import com.example.Restaurant_Management_System_REST_API.service.RestaurantOrderMenuRecordService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final RestaurantOrderMenuRecordService restaurantOrderMenuRecordService;

    //This part is intended to be used by entire staff (waitress, kitchen staff, manager and owner) and is covered with spring security
    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "info/inventory/stockAmount/greaterThan", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) //MIME binary data type
    public ResponseEntity<StreamingResponseBody> getInventoryItemByAmountGreaterThan(@RequestParam("amount") double amount) {

        StreamingResponseBody stream = reportService.getInventoryItemByAmountGreaterThan(amount);

        //Because in headers I didn't have a Content-Disposition I got response a zip file with html and xml files.
        //When I add below I manually add the header Content-Disposition. In this case I got a response as report xls file
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=InventoryItems.xlsx");

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

        StreamingResponseBody stream = reportService.getRestaurantOrderByOrderTimeRange(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=restaurantOrdersByTimeRange.xlsx");

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping(value = "info/restaurantOrder/findByOrderStatus", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getRestaurantOrderByOrderStatus(@RequestParam("order_status") OrderStatus orderStatus) {

        List<RestaurantOrder> restaurantOrderByOrderStatus = reportService.getRestaurantOrderByOrderStatus(orderStatus);

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
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=restaurantOrdersByOrderStatus.xlsx");

        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping("info/restaurantOrder/findByTable")
    public List<RestaurantOrder> getRestaurantOrderByTable(@RequestParam("table_id") Long id) {
        return reportService.getRestaurantOrderByTable(id);
    }

    @GetMapping("info/restaurantOrder/findByTableAndTimePeriod")
    public List<RestaurantOrder> getRestaurantOrderByTableAndTimeRange(@RequestParam("table_id") Long id,
                                                                       @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
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

    @GetMapping(value = "report/customer/findByRole", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> getCustomerByRole(@RequestParam("role") String roleName) {

        List<Customer> customerByRole = reportService.getCustomerByRole(roleName);

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("customersByRoles");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Customer ID");
            headerRow.createCell(1).setCellValue("Creation time");
            headerRow.createCell(2).setCellValue("Reservation ID");
            headerRow.createCell(3).setCellValue("Customer name");
            headerRow.createCell(4).setCellValue("Account enabled");
            headerRow.createCell(5).setCellValue("Roles");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < customerByRole.size(); i++) {
                Customer customer = customerByRole.get(i);
                Collection<? extends GrantedAuthority> authorities = customer.getAuthorities();

                // Collect all roles into a single string with commas
                String roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "));

                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getCreationTime().format(formatter));
                if (customer.getReservation() != null)
                    row.createCell(2).setCellValue(customer.getReservation().getId());
                row.createCell(3).setCellValue(customer.getContactDetails().getName());
                row.createCell(4).setCellValue(customer.getEnabled());
                row.createCell(5).setCellValue(roles);
            }
            workbook.write(outputStream);
            workbook.close();
        };

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", "attachment; filename=customerByRoles.xlsx");

        return new ResponseEntity<>(stream, header, HttpStatus.OK);
    }

    @GetMapping("report/restaurantOrder/findTotalSumInPeriodTime")
    public double getTotalSumRestaurantOrdersInPeriodTime(@RequestParam("time_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeFrom,
                                                          @RequestParam("time_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeTo) {
        return reportService.getTotalSumRestaurantOrdersInPeriodTime(timeFrom, timeTo);
    }

    @GetMapping("report/getPopularDishes/inPeriod")
    public ResponseEntity<StreamingResponseBody> getPopularDishesInTimePeriod(@RequestParam("time_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeFrom,
                                                                              @RequestParam("time_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate timeTo) {

        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = reportService.getRestaurantOrderMenuRecordInTimePeriod(timeFrom, timeTo);

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("popularDishes");

            Row periodRow = sheet.createRow(0);
            periodRow.createCell(0).setCellValue("Time period: " + timeFrom + " - " + timeTo);

            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("Dish name");
            headerRow.createCell(1).setCellValue("Total portions ordered");

            Map<String, Double> dishPortionsMap = new HashMap<>();
            for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord : restaurantOrderMenuRecordList) {
                String dishName = eachRestaurantOrderMenuRecord.getMenuRecord().getName();
                Double portions = eachRestaurantOrderMenuRecord.getPortionsAmount();
                //This below is retrieving the current total portions for the dish from the map. If the dish is not yet in the map
                // (this is the first time seeing this dish), it returns a default value of 0.0.
                //... + portions: This is adding the portions of the current RestaurantOrderMenuRecord to the total portions retrieved from the map.
                dishPortionsMap.put(dishName, dishPortionsMap.getOrDefault(dishName, 0.0) + portions);
            }

            int rowIndex = 2;  // This variable is used to keep track of which row in the Excel sheet the code is currently writing to.
            //below I iterate over each entry in the dishPortionsMap
            for (Map.Entry<String, Double> entry : dishPortionsMap.entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            workbook.write(outputStream);
            workbook.close();
        };
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", "attachment; filename=popularDishes.xlsx");

        return new ResponseEntity<>(stream, header, HttpStatus.OK);
    }


}
