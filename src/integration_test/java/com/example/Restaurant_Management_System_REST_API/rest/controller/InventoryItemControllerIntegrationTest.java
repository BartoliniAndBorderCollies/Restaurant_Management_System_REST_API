package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //JUnit will create only one instance of the test class
// (this is called per-class test instance lifecycle), and the @BeforeAll method doesn’t need to be static.
class InventoryItemControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicAuthHeaderStaff;

    @BeforeAll
    void setUpRolesAndCustomers() {

        String originalPassword = "lala";
        String encodedPassword = passwordEncoder.encode(originalPassword); //I use PasswordEncoder in SecurityConfig therefore it expects encoded password

        //Creation of different authorities and saving it to database
        Authority authorityStaff = new Authority(null, "ROLE_STAFF");
        authorityRepository.save(authorityStaff);

        //Creation of Sets and adding authorities to Sets
        Set<Authority> authoritiesStaff = new HashSet<>();
        authoritiesStaff.add(authorityStaff);

        //Creation of customers and saving it to database
        Customer staff = new Customer(null, LocalDateTime.now(), null, null, encodedPassword,
                true, true, true, true, "staff@onet.eu",
                authoritiesStaff);
        customerRepository.save(staff);

        //Defining basicAuthHeader required for authorization in the integration test
        basicAuthHeaderStaff = "Basic " + Base64.getEncoder()
                .encodeToString((staff.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password
    }

    @Test
    public void create_ShouldCreateInventoryItemInDbAndReturnDTOResponse_WhenInventoryItemDTORequestIsGiven() {

        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest(null, null,
                100, null, "Potatoes", "Potatoes", 1.49);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(null, null, 100,
                null, "Potatoes", "Potatoes", 1.49 );

        webTestClient.post()
                .uri("/api/inventory/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .bodyValue(inventoryItemDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryItemDTOResponse.class)
                .consumeWith(response -> {
                    InventoryItemDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertNotNull(actualResponse.getId());
                    assertEquals(expected.getStockAmount(), actualResponse.getStockAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
    }

}