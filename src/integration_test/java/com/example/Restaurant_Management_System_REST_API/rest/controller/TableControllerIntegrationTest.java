package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import org.junit.jupiter.api.AfterAll;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TableControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicAuthHeaderOwner;
    @Autowired
    private TableRepository tableRepository;

    @BeforeAll
    public void setUpRolesAndCustomers() {
        //Setting authorities
        Authority authority = new Authority(null, "ROLE_OWNER");
        authorityRepository.save(authority);
        Set<Authority> authoritySet = new HashSet<>();
        authoritySet.add(authority);

        //Setting password
        String rawPassword = "Lala!5";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        //Setting Customer
        ContactDetails contactDetails = new ContactDetails("Owner", "Wiosenna", "14", "Poznań", "11-015",
                "123456789");
        Customer owner = new Customer(null, LocalDateTime.now(), null, contactDetails, encodedPassword, true, true, true, true,
                "owner@customer.eu", authoritySet);
        customerRepository.save(owner);

        //Defining basicAuthHeader required for authorization in the integration test
        basicAuthHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((owner.getEmailAddress() + ":" + rawPassword).getBytes()); // here I need to provide a raw password
    }

    @AfterAll
    public void cleanDatabase() {
        tableRepository.deleteAll();
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    @Test
    public void add_ShouldAddRestaurantTableToDatabaseAndReturnTableDTO_WhenTableDTOIsGiven() {
        TableDTO tableDTO = new TableDTO(1L);

        webTestClient.post()
                .uri("/api/table/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(tableDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TableDTO.class)
                .consumeWith(response -> {
                    TableDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(tableDTO, actualResponse);
                });
        tableRepository.deleteAll();
    }

}