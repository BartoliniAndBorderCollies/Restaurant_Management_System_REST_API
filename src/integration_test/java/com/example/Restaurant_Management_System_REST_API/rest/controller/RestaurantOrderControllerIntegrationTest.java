package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
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
    private RestaurantOrderDTO restaurantOrderDTO;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicHeaderOwner;
    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;
    private Customer restaurantOwner;
    @Autowired
    private ModelMapper modelMapper;
    private LocalDateTime time;
    private Table table;
    @Autowired
    private TableRepository tableRepository;
    private TableReservationDTO tableDTO;

    @BeforeEach
    public void prepareRestaurantTableAndMapToDTO() {
        table = new Table(null, true, new ArrayList<>(), new ArrayList<>());
        tableRepository.save(table);
        tableDTO = modelMapper.map(table, TableReservationDTO.class);
    }

    @BeforeEach
    public void setUpRestaurantOrderDTO() {
        time = LocalDateTime.of(2020, 10, 10, 19, 55);
        restaurantOrderDTO = new RestaurantOrderDTO(null, time, OrderStatus.PENDING, tableDTO, null); //TODO: add records after mering new branch
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

        restaurantOwner = new Customer(null, LocalDateTime.now(), null, contactDetails, encodedPassword, true, true,
                true, true, "owner@owner.eu", authorities);
        customerRepository.save(restaurantOwner);
        basicHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((restaurantOwner.getEmailAddress() + ":" + rawPassword).getBytes());
    }

    @AfterEach
    public void cleanDatabase() {
        restaurantOrderRepository.deleteAll();
    }

    @AfterAll
    public void cleanRoles() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        tableRepository.deleteAll();
    }

    @Test
    public void add_ShouldAddRestaurantOrderToDatabaseAndReturnRestaurantOrderDTO_WhenRestaurantOrderDTOIsGiven() {

        webTestClient.post()
                .uri("/api/order/add")
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .bodyValue(restaurantOrderDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(restaurantOrderDTO.getOrderTime(), actualResponse.getOrderTime());
                    assertEquals(restaurantOrderDTO.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(restaurantOrderDTO.getTable(), actualResponse.getTable());
                    assertEquals(restaurantOrderDTO.getMenuRecords(), actualResponse.getMenuRecords());
                });
    }

    @Test
    public void findById_ShouldFindAndReturnRestaurantOrderDTO_WhenRestaurantOrderExistAndIdIsGiven() {
        RestaurantOrder restaurantOrder = new RestaurantOrder(null, time, OrderStatus.DONE, restaurantOwner,
                table, new ArrayList<>()); //TODO: add menu records when new branch will be merged
        restaurantOrderRepository.save(restaurantOrder);

        webTestClient.get()
                .uri("/api/order/find/" + restaurantOrder.getId())
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(restaurantOrder.getOrderTime(), actualResponse.getOrderTime());
                    assertEquals(restaurantOrder.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(tableDTO, actualResponse.getTable());
                    //TODO: add more assertions when new branch will be merged
                });
    }

    @Test
    public void findAll_ShouldReturnRestaurantOrderDTOList_WhenRestaurantOrdersExist() {
        RestaurantOrder restaurantOrder = new RestaurantOrder(null, time, OrderStatus.DONE, restaurantOwner,
                table, new ArrayList<>()); //TODO: add menu records when new branch will be merged
        RestaurantOrder restaurantOrder2 = new RestaurantOrder(null, time, OrderStatus.PENDING, restaurantOwner,
                null, new ArrayList<>()); //TODO: add menu records when new branch will be merged
        restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderRepository.save(restaurantOrder2);

        List<RestaurantOrder> restaurantOrderList = Arrays.asList(restaurantOrder, restaurantOrder2);
        List<RestaurantOrderDTO> expected = new ArrayList<>();
        restaurantOrderList.forEach(order -> expected.add(modelMapper.map(order, RestaurantOrderDTO.class)));

        webTestClient.get()
                .uri("/api/order/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RestaurantOrderDTO.class)
                .consumeWith(response -> {
                    List<RestaurantOrderDTO> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);
                });
    }

    @Test
    public void update_ShouldUpdateRestaurantOrderAndSaveAndReturnUpdatedRestaurantOrderDTO_WhenRestaurantOrderDTOAndIdAreGIven() {
        //This restaurant order exists in db and will be updated:
        RestaurantOrder restaurantOrder = new RestaurantOrder(null, time, OrderStatus.PENDING, restaurantOwner,
                table, new ArrayList<>()); //TODO: add menu records when new branch will be merged
        restaurantOrderRepository.save(restaurantOrder);

        //These are expected update details:
        Table updatedTable = new Table(100L, false, new ArrayList<>(), new ArrayList<>());
        tableRepository.save(updatedTable);
        TableReservationDTO updatedTableDTO = modelMapper.map(updatedTable, TableReservationDTO.class);
        OrderStatus updatedStatus = OrderStatus.DONE;
        RestaurantOrderDTO expected = new RestaurantOrderDTO(null, time, updatedStatus, updatedTableDTO, new ArrayList<>());

        //This is a body value of updating DTO
        RestaurantOrderDTO updatingDTO = new RestaurantOrderDTO(null, time, updatedStatus, updatedTableDTO, new ArrayList<>());

        webTestClient.put()
                .uri("/api/order/update/" + restaurantOrder.getId())
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .bodyValue(updatingDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RestaurantOrderDTO.class)
                .consumeWith(response -> {
                    RestaurantOrderDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getOrderStatus(), actualResponse.getOrderStatus());
                    assertEquals(expected.getTable(), actualResponse.getTable());
                });
    }

    @Test
    public void delete_ShouldDeleteRestaurantOrderFromDatabase_WhenRestaurantOrderIdIsGiven() {
        RestaurantOrder restaurantOrder = new RestaurantOrder(null, time, OrderStatus.DONE, restaurantOwner,
                table, new ArrayList<>()); //TODO: add menu records when new branch will be merged
        restaurantOrderRepository.save(restaurantOrder);

        webTestClient.delete()
                .uri("/api/order/delete/" + restaurantOrder.getId())
                .header(HttpHeaders.AUTHORIZATION, basicHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals("Order number " + restaurantOrder.getId() + " has been deleted!", actualResponse);
                    assertEquals(HttpStatus.OK, response.getStatus());
                    Optional<RestaurantOrder> shouldBeDeleted = restaurantOrderRepository.findById(restaurantOrder.getId());
                    assertTrue(shouldBeDeleted.isEmpty());
                });
    }

}