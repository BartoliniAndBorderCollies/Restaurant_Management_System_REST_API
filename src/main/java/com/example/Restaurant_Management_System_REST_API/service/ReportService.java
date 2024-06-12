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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    private void createCell(Row row, int cellIndex, Object value) {
        Cell cell = row.createCell(cellIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }
    }

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
            createCell(headerRow, 0, "ID");
            createCell(headerRow, 1, "Name");
            createCell(headerRow, 2, "Description");
            createCell(headerRow, 3, "Price");
            createCell(headerRow, 4, "Amount");
            createCell(headerRow, 5, "Supplier Name");
            createCell(headerRow, 6, "Supplier Street");
            createCell(headerRow, 7, "Supplier House Number");
            createCell(headerRow, 8, "Supplier City");
            createCell(headerRow, 9, "Supplier Postal Code");
            createCell(headerRow, 10, "Supplier Telephone Number");

            // Fill data rows
            for (int i = 0; i < items.size(); i++) {
                InventoryItem item = items.get(i);
                Supplier supplier = item.getSupplier();
                ContactDetails contactDetails = supplier.getContactDetails();
                Row row = sheet.createRow(i + 1);
                createCell(row, 0, item.getId());
                createCell(row, 1, item.getName());
                createCell(row, 2, item.getAmount());
                createCell(row, 3, contactDetails.getName());
                createCell(row, 4, contactDetails.getStreet());
                createCell(row, 5, contactDetails.getName());
                createCell(row, 6, contactDetails.getStreet());
                createCell(row, 7, contactDetails.getHouseNumber());
                createCell(row, 8, contactDetails.getCity());
                createCell(row, 9, contactDetails.getPostalCode());
                createCell(row, 10, contactDetails.getTelephoneNumber());
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
    public StreamingResponseBody getCustomerByRole(String roleName) {

        List<Customer> customerByRole = customerRepository.findByAuthorities_Authority_name(roleName);

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("customersByRoles");

            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "Customer ID");
            createCell(headerRow, 1, "Creation time");
            createCell(headerRow, 2, "Reservation ID");
            createCell(headerRow, 3, "Customer name");
            createCell(headerRow, 4, "Account enabled");
            createCell(headerRow, 5, "Roles");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < customerByRole.size(); i++) {
                Customer customer = customerByRole.get(i);
                Collection<? extends GrantedAuthority> authorities = customer.getAuthorities();

                // Collect all roles into a single string with commas
                String roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "));

                Row row = sheet.createRow(i + 1);
                createCell(row, 0, customer.getId());
                createCell(row, 1, customer.getCreationTime().format(formatter));
                if (customer.getReservation() != null)
                    createCell(row, 2, customer.getReservation().getId());
                createCell(row, 3, customer.getContactDetails().getName());
                createCell(row, 4, customer.getEnabled());
                createCell(row, 5, roles);
            }
            workbook.write(outputStream);
            workbook.close();
        };

        return stream;
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
            createCell(headerRow, 0, "Restaurant Order ID");
            createCell(headerRow, 1, "Order time");
            createCell(headerRow, 2, "Order status");
            createCell(headerRow, 3, "Table id");
            createCell(headerRow, 4, "Telephone number");
            createCell(headerRow, 5, "Total amount to pay");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Fill data rows
            for (int i = 0; i < restaurantOrderByOrderTimeRange.size(); i++) {
                RestaurantOrder restaurantOrder = restaurantOrderByOrderTimeRange.get(i);
                OrderStatus orderStatus = restaurantOrder.getOrderStatus();

                Row row = sheet.createRow(i + 1);
                createCell(row, 0, restaurantOrder.getId());
                createCell(row, 1, restaurantOrder.getOrderTime().format(formatter));
                createCell(row, 2, orderStatus.toString());
                createCell(row, 3, restaurantOrder.getTable().getId());
                createCell(row, 4, restaurantOrder.getTelephoneNumber());
                createCell(row, 5, restaurantOrder.getTotalAmountToPay());

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
            createCell(headerRow, 0, "Restaurant Order ID");
            createCell(headerRow, 1, "Order time");
            createCell(headerRow, 2, "Order status");
            createCell(headerRow, 3, "Table id");
            createCell(headerRow, 4, "Telephone number");
            createCell(headerRow, 5, "Total amount to pay");
            createCell(headerRow, 6, "Menu record name");
            createCell(headerRow, 7, "Portions amount");

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
                    createCell(row, 0, restaurantOrder.getId());
                    createCell(row, 1, restaurantOrder.getOrderTime().format(formatter));
                    createCell(row, 2, restaurantOrder.getOrderStatus().toString());
                    createCell(row, 3, restaurantOrder.getTable().getId());
                    createCell(row, 4, restaurantOrder.getTelephoneNumber());
                    createCell(row, 5, restaurantOrder.getTotalAmountToPay());
                    createCell(row, 6, menuRecord.getName());
                    createCell(row, 7, portionsAmount);

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

    public StreamingResponseBody getRestaurantOrderMenuRecordInTimePeriod(LocalDate timeFrom, LocalDate timeTo) {

        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByTimePeriod(timeFrom.atStartOfDay(),
                timeTo.plusDays(1).atStartOfDay());

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("popularDishes");

            Row periodRow = sheet.createRow(0);
            createCell(periodRow, 0, "Time period: " + timeFrom + " - " + timeTo);

            Row headerRow = sheet.createRow(1);
            createCell(headerRow, 0, "Dish name");
            createCell(headerRow, 1, "Total portions ordered");

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
                createCell(row, 0, entry.getKey());
                createCell(row, 1, entry.getValue());
            }

            workbook.write(outputStream);
            workbook.close();
        };

        return stream;
    }
}
