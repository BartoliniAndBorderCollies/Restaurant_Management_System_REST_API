package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.RestaurantOrderDTOs.RestaurantOrderDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

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

    @BeforeEach
    public void setUpRestaurantOrderDTO() {
        LocalDateTime time = LocalDateTime.of(2020, 10, 10, 19, 55);
        restaurantOrderDTO = new RestaurantOrderDTO(null, time, OrderStatus.PENDING, null, null); //TODO: add tables and records after mering new branch
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

    @AfterAll
    public void cleanDatabase() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        restaurantOrderRepository.deleteAll();
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

}