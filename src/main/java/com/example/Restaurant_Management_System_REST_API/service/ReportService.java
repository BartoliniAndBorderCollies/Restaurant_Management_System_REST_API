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
import com.example.Restaurant_Management_System_REST_API.util.*;
import lombok.AllArgsConstructor;
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
    private final CellValueSetter<String> stringSetter = new StringValueSetter();
    private final CellValueSetter<Long> longSetter = new LongValueSetter();
    private final CellValueSetter<Double> doubleSetter = new DoubleValueSetter();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final String ID = "ID";
    private static final String NAME = "Name";
    private static final String DESCRIPTION = "Description";
    private static final String PRICE = "Price";
    private static final String AMOUNT = "Amount";
    private static final String SUPPLIER_NAME = "Supplier name";
    private static final String SUPPLIER_STREET = "Supplier street";
    private static final String SUPPLIER_HOUSE_NUMBER = "Supplier house number";
    private static final String SUPPLIER_CITY = "Supplier city";
    private static final String SUPPLIER_POSTAL_CODE = "Supplier postal code";
    private static final String SUPPLIER_TELEPHONE_NUMBER = "Supplier telephone number";
    private static final String CUSTOMER_ID = "Customer ID";
    private static final String CUSTOMER_NAME = "Customer name";
    private static final String CUSTOMER_TELEPHONE_NUMBER = "Telephone number";
    private static final String CREATION_TIME = "Creation time";
    private static final String RESERVATION_ID = "Reservation ID";
    private static final String ORDER_TIME = "Order time";
    private static final String ORDER_STATUS = "Order status";
    private static final String ORDER_TOTAL_AMOUNT_TO_PAY = "Total amount to pay";
    private static final String RESTAURANT_ORDER_ID = "Restaurant Order ID";
    private static final String TABLE_ID = "Table ID";
    private static final String MENU_RECORD_NAME = "Menu record name";
    private static final String ACCOUNT_ENABLED = "Account Enabled";
    private static final String PORTIONS_AMOUNT = "Portions amount";
    private static final String ROLES = "Roles";
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String REPORT_INVENTORY_ITEMS = "Inventory items";
    private static final String REPORT_CUSTOMER_BY_ROLES = "Customer by roles";
    private static final String REPORT_RESTAURANT_ORDERS_BY_TIME_RANGE = "Restaurant orders by time range";
    private static final String REPORT_RESTAURANT_ORDERS_BY_ORDER_STATUS = "Restaurant orders by order status";
    private static final String REPORT_POPULAR_DISHES = "Popular dishes";

    //section for InventoryItem reports
    public StreamingResponseBody getInventoryItemByAmountGreaterThan(double amount) {

        List<InventoryItem> items = inventoryItemRepository.findByAmountGreaterThan(amount);

        // The StreamingResponseBody is used to stream the response back to the client. This is particularly useful for large files which can’t be held in memory.

        //Below I create an excel file using Apache POI library and then I write it to outputStream for the client to download it
        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook(); // a top level object to create sheets and other operations
            Sheet sheet = workbook.createSheet(REPORT_INVENTORY_ITEMS);

            String[] headers = {ID, NAME, DESCRIPTION, PRICE, AMOUNT, SUPPLIER_NAME, SUPPLIER_STREET, SUPPLIER_HOUSE_NUMBER,
            SUPPLIER_CITY, SUPPLIER_POSTAL_CODE, SUPPLIER_TELEPHONE_NUMBER};

            // Create header row
            Row headerRow = sheet.createRow(0);

            for (int i =0; i< headers.length; i ++) {
                stringSetter.setCellValue(headerRow, i, headers[i]);
            }

            // Fill data rows
            for (int i = 0; i < items.size(); i++) {
                InventoryItem item = items.get(i);
                Supplier supplier = item.getSupplier();
                ContactDetails supplierContactDetails = supplier.getContactDetails();
                Row row = sheet.createRow(i + 1);
                longSetter.setCellValue(row, 0, item.getId());
                stringSetter.setCellValue(row, 1, item.getName());
                stringSetter.setCellValue(row, 2, item.getDescription());
                doubleSetter.setCellValue(row, 3, item.getPrice());
                doubleSetter.setCellValue(row, 4, item.getAmount());
                stringSetter.setCellValue(row, 5, supplierContactDetails.getName());
                stringSetter.setCellValue(row, 6, supplierContactDetails.getStreet());
                stringSetter.setCellValue(row, 7, supplierContactDetails.getHouseNumber());
                stringSetter.setCellValue(row, 8, supplierContactDetails.getCity());
                stringSetter.setCellValue(row, 9, supplierContactDetails.getPostalCode());
                stringSetter.setCellValue(row, 10, supplierContactDetails.getTelephoneNumber());
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
            Sheet sheet = workbook.createSheet(REPORT_CUSTOMER_BY_ROLES);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String [] headers = {CUSTOMER_ID, CREATION_TIME, RESERVATION_ID, CUSTOMER_NAME, ACCOUNT_ENABLED, ROLES};

            for(int i = 0; i<headers.length; i++) {
                stringSetter.setCellValue(headerRow, i, headers[i]);
            }

            // Fill data rows
            for (int i = 0; i < customerByRole.size(); i++) {
                Customer customer = customerByRole.get(i);
                Collection<? extends GrantedAuthority> authorities = customer.getAuthorities();

                // Collect all roles into a single string with commas
                String roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "));

                Row row = sheet.createRow(i + 1);
                longSetter.setCellValue(row, 0, customer.getId());
                stringSetter.setCellValue(row, 1, customer.getCreationTime().format(formatter));
                if (customer.getReservation() != null)
                    longSetter.setCellValue(row, 2, customer.getReservation().getId());
                stringSetter.setCellValue(row, 3, customer.getContactDetails().getName());
                stringSetter.setCellValue(row, 4, customer.getEnabled().toString());
                stringSetter.setCellValue(row, 5, roles);
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
            Sheet sheet = workbook.createSheet(REPORT_RESTAURANT_ORDERS_BY_TIME_RANGE);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String [] headers = {RESTAURANT_ORDER_ID, ORDER_TIME, ORDER_STATUS, TABLE_ID, CUSTOMER_TELEPHONE_NUMBER, ORDER_TOTAL_AMOUNT_TO_PAY};
            for(int i = 0; i< headers.length; i++)
                stringSetter.setCellValue(headerRow, i, headers[i]);

            // Fill data rows
            for (int i = 0; i < restaurantOrderByOrderTimeRange.size(); i++) {
                RestaurantOrder restaurantOrder = restaurantOrderByOrderTimeRange.get(i);
                OrderStatus orderStatus = restaurantOrder.getOrderStatus();

                Row row = sheet.createRow(i + 1);
                longSetter.setCellValue(row, 0, restaurantOrder.getId());
                stringSetter.setCellValue(row, 1, restaurantOrder.getOrderTime().format(formatter));
                stringSetter.setCellValue(row, 2, orderStatus.toString());
                longSetter.setCellValue(row, 3, restaurantOrder.getTable().getId());
                stringSetter.setCellValue(row, 4, restaurantOrder.getTelephoneNumber());
                doubleSetter.setCellValue(row, 5, restaurantOrder.getTotalAmountToPay());

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
            Sheet sheet = workbook.createSheet(REPORT_RESTAURANT_ORDERS_BY_ORDER_STATUS);

            Row headerRow = sheet.createRow(0);
            String [] headers = {RESTAURANT_ORDER_ID, ORDER_TIME, ORDER_STATUS, TABLE_ID, CUSTOMER_TELEPHONE_NUMBER,
                    ORDER_TOTAL_AMOUNT_TO_PAY, MENU_RECORD_NAME, PORTIONS_AMOUNT};
            for(int i = 0; i< headers.length; i++)
                stringSetter.setCellValue(headerRow, i, headers[i]);


            //fill the data
            int rowIndex = 1;
            for (int i = 0; i < restaurantOrderByOrderStatus.size(); i++) {
                RestaurantOrder restaurantOrder = restaurantOrderByOrderStatus.get(i);
                List<RestaurantOrderMenuRecord> restaurantOrders = restaurantOrder.getRestaurantOrders();

                for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord : restaurantOrders) {
                    MenuRecord menuRecord = eachRestaurantOrderMenuRecord.getMenuRecord();
                    Double portionsAmount = eachRestaurantOrderMenuRecord.getPortionsAmount();

                    Row row = sheet.createRow(rowIndex++);// this adds new row if restaurant order has multiple orders
                    longSetter.setCellValue(row, 0, restaurantOrder.getId());
                    stringSetter.setCellValue(row, 1, restaurantOrder.getOrderTime().format(formatter));
                    stringSetter.setCellValue(row, 2, restaurantOrder.getOrderStatus().toString());
                    longSetter.setCellValue(row, 3, restaurantOrder.getTable().getId());
                    stringSetter.setCellValue(row, 4, restaurantOrder.getTelephoneNumber());
                    doubleSetter.setCellValue(row, 5, restaurantOrder.getTotalAmountToPay());
                    stringSetter.setCellValue(row, 6, menuRecord.getName());
                    doubleSetter.setCellValue(row, 7, portionsAmount);
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

    public StreamingResponseBody getRestaurantOrderMenuRecordInTimePeriod(LocalDate dateFrom, LocalDate dateTo) {

        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByTimePeriod(dateFrom.atStartOfDay(),
                dateTo.plusDays(1).atStartOfDay());

        StreamingResponseBody stream = outputStream -> {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(REPORT_POPULAR_DISHES);

            Row periodRow = sheet.createRow(0);
            stringSetter.setCellValue(periodRow, 0, "Time period: " + dateFrom + " - " + dateTo);

            Row headerRow = sheet.createRow(1);
            stringSetter.setCellValue(headerRow, 0, MENU_RECORD_NAME);
            stringSetter.setCellValue(headerRow, 1, PORTIONS_AMOUNT);

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
                stringSetter.setCellValue(row, 0, entry.getKey());
                doubleSetter.setCellValue(row, 1, entry.getValue());
            }

            workbook.write(outputStream);
            workbook.close();
        };

        return stream;
    }
}
