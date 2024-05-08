package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordForOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderRequestDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.*;
import com.example.Restaurant_Management_System_REST_API.repository.*;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantOrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private RestaurantOrderResponseDTO restaurantOrderResponseDTO;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicHeaderOwner;
    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;
    @Autowired
    private ModelMapper modelMapper;
    private LocalDateTime time;
    private Table table;
    @Autowired
    private TableRepository tableRepository;
    private TableReservationDTO tableDTO;
    private MenuRecord chopWithPotatoes;
    private List<MenuRecordForOrderDTO> menuRecordForOrderDTOList;
    @Autowired
    private MenuRecordRepository menuRecordRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private RestaurantOrderMenuRecordRepository restaurantOrderMenuRecordRepository;
    private RestaurantOrder restaurantOrder;
    private RestaurantOrderMenuRecord restaurantOrderMenuRecord;


    @BeforeAll
    public void prepareEnvironment() {
        time = LocalDateTime.of(2020, 10, 10, 19, 55);
        table = new Table(null, true, null, null);
        tableRepository.save(table);
        tableDTO = modelMapper.map(table, TableReservationDTO.class);
        menuRecordForOrderDTOList = new ArrayList<>();
        MenuRecordForOrderDTO menuRecordForOrderDTO = new MenuRecordForOrderDTO(7L, "Chop with potatoes and pickles", 2.0);
        MenuRecordForOrderDTO menuRecordForOrderDTO2 = new MenuRecordForOrderDTO(8L, "Lech beer 0.5", 2.0);
        menuRecordForOrderDTOList.add(menuRecordForOrderDTO);
        menuRecordForOrderDTOList.add(menuRecordForOrderDTO2);
    }

    @BeforeAll
    public void prepareMenuRecordsSupplierAndInventoryItem() {

        Ingredient lechBeerIngredient = new Ingredient("Lech beer", 1);
        Ingredient porkMeat = new Ingredient("Pork meat", 0.3);
        Ingredient potatoes = new Ingredient("Potatoes", 0.5);
        Ingredient pickles = new Ingredient("Pickles", 0.4);
        List<Ingredient> chopWithPotatoesIngredientsList = Arrays.asList(porkMeat, potatoes, pickles);
        List<Ingredient> lechBeerIngredientsList = Arrays.asList(lechBeerIngredient);

        chopWithPotatoes = new MenuRecord(chopWithPotatoesIngredientsList, Category.MAIN_DISH,
                "Chop with potatoes and pickles", "Yumiiii", 15.0, true);
        MenuRecord lechBeer = new MenuRecord(lechBeerIngredientsList, Category.BEVERAGE, "Lech beer 0.5",
                "0,5L", 7.0, true);

        menuRecordRepository.save(chopWithPotatoes);
        menuRecordRepository.save(lechBeer);

        ContactDetails contactDetails = new ContactDetails("Lidl", "Lidlowa", "14", "Poznań",
                "11-015", "123456789");

        Supplier lidlSupplier = new Supplier(null, contactDetails, new ArrayList<>());
        supplierRepository.save(lidlSupplier);

        InventoryItem lechBeerInventoryItem = new InventoryItem(null, 100, lidlSupplier, "Lech beer",
                "0,5L", 1.99);
        InventoryItem porkMeatInventoryItem = new InventoryItem(null, 100, lidlSupplier, "Pork meat",
                "fresh nice meat!", 3.99);
        InventoryItem potatoesInventoryItem = new InventoryItem(null, 100, lidlSupplier, "Potatoes",
                "Potatoes", 0.99);
        InventoryItem picklesInventoryItem = new InventoryItem(null, 100, lidlSupplier, "Pickles",
                "Cabbage and so on", 0.59);
        inventoryItemRepository.save(lechBeerInventoryItem);
        inventoryItemRepository.save(porkMeatInventoryItem);
        inventoryItemRepository.save(potatoesInventoryItem);
        inventoryItemRepository.save(picklesInventoryItem);
    }

    @BeforeAll
    public void setUpRolesAndAuthorities() {
        ContactDetails contactDetails = new ContactDetails("owner", "Wiosenna", "14", "Gdańsk",
                "11-015", "1234567890");

        String rawPassword = "Lalka!1%";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Authority owner = new Authority(null, "ROLE_OWNER");
        authorityRepository.save(owner);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(owner);

        Customer restaurantOwner = new Customer(null, LocalDateTime.now(), null, contactDetails, encodedPassword, true, true,
                true, true, "owner@owner.eu", authorities);
        customerRepository.save(restaurantOwner);
        basicHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((restaurantOwner.getEmailAddress() + ":" + rawPassword).getBytes());
    }

    @BeforeEach
    public void prepareRestaurantOrder() {
        double amountToPay = 10.0;
        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordList = new ArrayList<>();
        restaurantOrder = new RestaurantOrder(null, time, OrderStatus.PENDING, table,
                "1234567890", amountToPay, null);
        restaurantOrderRepository.save(restaurantOrder);

        restaurantOrderMenuRecord = new RestaurantOrderMenuRecord(null, chopWithPotatoes,
                restaurantOrder, 1.0);
        restaurantOrderMenuRecordRepository.save(restaurantOrderMenuRecord);
        restaurantOrderMenuRecordList.add(restaurantOrderMenuRecord);

        restaurantOrder.setRestaurantOrders(restaurantOrderMenuRecordList);
        restaurantOrderRepository.save(restaurantOrder);
    }

    @AfterEach
    public void cleanRestaurantOrders() {
        restaurantOrderMenuRecordRepository.deleteAll();
        restaurantOrderRepository.deleteAll();
    }

    @AfterAll
    public void cleanDatabases() {
        restaurantOrderMenuRecordRepository.deleteAll();
        restaurantOrderRepository.deleteAll();
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        tableRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        menuRecordRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    public void add_ShouldAddRestaurantOrderToDatabaseAndReturnRestaurantOrderDTO_WhenRestaurantOrderDTOIsGiven() {
        RestaurantOrderRequestDTO restaurantOrderRequestDTO = new RestaurantOrderRequestDTO(OrderStatus.PENDING, tableDTO,
                "1234567890", menuRecordForOrderDTOList);
        double amountToPay = 44.0;

        webTestClient.post()
                .uri("/api/order/add")
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .bodyValue(restaurantOrderRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderResponseDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderResponseDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(restaurantOrderRequestDTO.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(restaurantOrderRequestDTO.getTable(), actualResponse.getTable());
                    assertEquals(amountToPay, actualResponse.getTotalAmountToPay());
                    assertEquals(restaurantOrderRequestDTO.getMenuRecords(), actualResponse.getMenuRecords());
                });
    }

    @Test
    public void findById_ShouldFindAndReturnRestaurantOrderDTO_WhenRestaurantOrderExistAndIdIsGiven() {

        webTestClient.get()
                .uri("/api/order/find/" + restaurantOrder.getId())
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderResponseDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderResponseDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(restaurantOrder.getOrderTime(), actualResponse.getOrderTime());
                    assertEquals(restaurantOrder.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(modelMapper.map(restaurantOrder.getTable(), TableReservationDTO.class), actualResponse.getTable());
                    assertEquals(restaurantOrder.getTelephoneNumber(), actualResponse.getTelephoneNumber());
                    assertEquals(restaurantOrder.getTotalAmountToPay(), actualResponse.getTotalAmountToPay());

                    List<RestaurantOrderMenuRecord> restaurantOrders = restaurantOrder.getRestaurantOrders();
                    List<MenuRecordForOrderDTO> expected = new ArrayList<>();
                    for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord: restaurantOrders) {
                        MenuRecord eachMenuRecord = eachRestaurantOrderMenuRecord.getMenuRecord();
                        MenuRecordForOrderDTO menuRecordForOrderDTO = modelMapper.map(eachMenuRecord, MenuRecordForOrderDTO.class);
                        menuRecordForOrderDTO.setPortionsAmount(restaurantOrderMenuRecord.getPortionsAmount());
                        expected.add(menuRecordForOrderDTO);
                    }

                    assertIterableEquals(expected, actualResponse.getMenuRecords());
                });
    }

    @Test
    public void findAll_ShouldReturnRestaurantOrderDTOList_WhenRestaurantOrdersExist() {
        double amountToPay = 10.0;

        List<MenuRecordForOrderDTO> menuRecordForOrderDTOList = new ArrayList<>();
        MenuRecord menuRecord = restaurantOrderMenuRecord.getMenuRecord();
        MenuRecordForOrderDTO menuRecordForOrderDTO = modelMapper.map(menuRecord, MenuRecordForOrderDTO.class);
        menuRecordForOrderDTO.setPortionsAmount(restaurantOrderMenuRecord.getPortionsAmount());
        menuRecordForOrderDTOList.add(menuRecordForOrderDTO);

        RestaurantOrderResponseDTO expectedRestaurantOrderResponseDTO1 = new RestaurantOrderResponseDTO(restaurantOrder.getId(), time,
                OrderStatus.PENDING, tableDTO, "1234567890", amountToPay, menuRecordForOrderDTOList);

        List<RestaurantOrderResponseDTO> expected = new ArrayList<>();
        expected.add(expectedRestaurantOrderResponseDTO1);

        webTestClient.get()
                .uri("/api/order/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RestaurantOrderResponseDTO.class)
                .consumeWith(response -> {
                    List<RestaurantOrderResponseDTO> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);
                });
    }

    @Test
    public void update_ShouldUpdateRestaurantOrderAndSaveAndReturnUpdatedRestaurantOrderDTO_WhenRestaurantOrderDTOAndIdAreGIven() {

        //These are expected update details:
        Table updatedTable = new Table(100L, false, new ArrayList<>(), new ArrayList<>());
        tableRepository.save(updatedTable);
        TableReservationDTO updatedTableDTO = modelMapper.map(updatedTable, TableReservationDTO.class);
        OrderStatus updatedStatus = OrderStatus.DONE;
        String updatedTelephoneNumber = "9999999999";

        RestaurantOrderResponseDTO expected = new RestaurantOrderResponseDTO(null, time, updatedStatus, updatedTableDTO,
                updatedTelephoneNumber, 0, new ArrayList<>());

        //This is a body value of updating DTO
        RestaurantOrderRequestDTO updatingDTO = new RestaurantOrderRequestDTO(updatedStatus, updatedTableDTO,
                updatedTelephoneNumber, new ArrayList<>());

        webTestClient.put()
                .uri("/api/order/update/" + restaurantOrder.getId())
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .bodyValue(updatingDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderResponseDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderResponseDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(expected.getTable(), actualResponse.getTable());
                    assertEquals(expected.getTelephoneNumber(), actualResponse.getTelephoneNumber());
                });
    }
//
//    @Test
//    public void delete_ShouldDeleteRestaurantOrderFromDatabase_WhenRestaurantOrderIdIsGiven() {
//        //This must be here because I create restaurantOrder in @BeforeAll and if I delete it here then I lost it for other methods
//        RestaurantOrder restaurantOrder = new RestaurantOrder(null, time, OrderStatus.DONE, table,
//                "1234567890", 0, new ArrayList<>()); //TODO: add menu records when new branch will be merged
//        restaurantOrderRepository.save(restaurantOrder);
//
//        webTestClient.delete()
//                .uri("/api/order/delete/" + restaurantOrder.getId())
//                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .consumeWith(response -> {
//                    String actualResponse = response.getResponseBody();
//                    assertNotNull(actualResponse);
//                    assertEquals("Order number " + restaurantOrder.getId() + " has been deleted!", actualResponse);
//                    assertEquals(HttpStatus.OK, response.getStatus());
//                    Optional<RestaurantOrder> shouldBeDeleted = restaurantOrderRepository.findById(restaurantOrder.getId());
//                    assertTrue(shouldBeDeleted.isEmpty());
//                });
//    }

}