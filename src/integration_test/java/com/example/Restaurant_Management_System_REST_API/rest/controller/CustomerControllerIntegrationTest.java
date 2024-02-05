package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
class CustomerControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Set<Authority> authorities = new HashSet<>();
    private String basicAuthHeaderOwner;
    private String encodedPassword;
    private String originalPassword;
    private Authority authorityOwner;

    @BeforeAll
    public void createAndSaveAuthority() {
        authorityOwner = new Authority(null, "ROLE_OWNER");
        authorityRepository.save(authorityOwner);

        originalPassword = "lala";
        encodedPassword = passwordEncoder.encode(originalPassword);
    }

    @BeforeEach
    public void setUp() {
        authorities.add(authorityOwner);

        Customer customerOwner = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true,
                "customer@test.eu", authorities);
        customerRepository.save(customerOwner);

        basicAuthHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((customerOwner.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password
    }


    @Test
    public void create_ShouldAddCustomerToDatabaseAndReturnCustomerDTO_WhenCustomerDTORequestIsGiven() {

        CustomerDTORequest customerDTORequest = new CustomerDTORequest(null, LocalDateTime.now(), null,
                null, "laleczka", true, true, true,
                true, "owner@test.eu", authorities);

        webTestClient.post()
                .uri("/api/customer/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(customerDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTOResponse.class)
                .consumeWith(response -> {
                    CustomerDTOResponse actualDTOResponse = response.getResponseBody();
                    assertNotNull(actualDTOResponse);
                    assertEquals(customerDTORequest.getReservation(), actualDTOResponse.getReservation());
                    assertEquals(customerDTORequest.getContactDetails(), actualDTOResponse.getContactDetails());
                    assertTrue(passwordEncoder.matches(customerDTORequest.getPassword(), actualDTOResponse.getPassword()));
                    assertEquals(customerDTORequest.getAccountNonExpired(), actualDTOResponse.getAccountNonExpired());
                    assertEquals(customerDTORequest.getAccountNonLocked(), actualDTOResponse.getAccountNonLocked());
                    assertEquals(customerDTORequest.getCredentialsNonExpired(), actualDTOResponse.getCredentialsNonExpired());
                    assertEquals(customerDTORequest.getEnabled(), actualDTOResponse.getEnabled());
                    assertEquals(customerDTORequest.getEmailAddress(), actualDTOResponse.getEmailAddress());
                    assertIterableEquals(customerDTORequest.getAuthorities(), actualDTOResponse.getAuthorities());
                });
    }

}