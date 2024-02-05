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
import org.modelmapper.ModelMapper;
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
    @Autowired
    private ModelMapper modelMapper;
    private final Set<Authority> authoritiesManagement = new HashSet<>();
    private final Set<Authority> authoritiesStaff = new HashSet<>();
    private String basicAuthHeaderOwner;
    private String basicAuthHeaderStaff;
    private String encodedPassword;
    private String originalPassword;
    private Authority authorityOwner;
    private Authority authorityStaff;
    private CustomerDTORequest customerDTORequest;

    @BeforeAll
    public void createAndSaveAuthority() {
        //Creation and saving of Authority
        authorityOwner = new Authority(null, "ROLE_OWNER");
        authorityStaff = new Authority(null, "ROLE_STAFF");

        authorityRepository.save(authorityOwner);
        authorityRepository.save(authorityStaff);

        //Adding Authority to Set<Authority>
        authoritiesManagement.add(authorityOwner);
        authoritiesStaff.add(authorityStaff);

        //Setting and encoding password
        originalPassword = "lala";
        encodedPassword = passwordEncoder.encode(originalPassword);

        //Creation and saving of customers
        Customer customerOwner = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true,"customer@test.eu", authoritiesManagement);
        customerRepository.save(customerOwner);

        Customer customerStaff = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, true, true, true, true,
                "staff@test.eu", authoritiesStaff);
        customerRepository.save(customerStaff);

        //Creation of headers
        basicAuthHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((customerOwner.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password

        basicAuthHeaderStaff = "Basic " + Base64.getEncoder().
                encodeToString((customerStaff.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password
    }

    @BeforeEach
    public void createCustomerDTORequest() {
        customerDTORequest = new CustomerDTORequest(null, LocalDateTime.now(), null,
                null, "laleczkaD1%", true, true, true,
                true, "owner@test.eu", authoritiesStaff);
    }

    @Test
    public void create_ShouldAddCustomerToDatabaseAndReturnCustomerDTO_WhenCustomerDTORequestIsGiven() {
        //Arrange - takes from @BeforeEach and @BeforeAll methods

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

    @Test
    public void findById_ShouldReturnAppropriateCustomerDTOResponse_WhenCustomerExistAndIdIsGiven() {

        String rawPassword = "laleczkaD1%";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Customer customer = new Customer(null, LocalDateTime.now(), null,
                null, encodedPassword, true, true, true,
                true, "owner@test.eu", authoritiesStaff);
        customerRepository.save(customer);

        webTestClient.get()
                .uri("/api/customer/find/" + customer.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTOResponse.class)
                .consumeWith(response -> {
                    CustomerDTOResponse actualResponseDTO = response.getResponseBody();
                    assertNotNull(actualResponseDTO);
                   assertEquals(customer.getId(), actualResponseDTO.getId());
                   assertEquals(customer.getReservation(), actualResponseDTO.getReservation());
                   assertEquals(customer.getContactDetails(), actualResponseDTO.getContactDetails());
                    assertTrue(passwordEncoder.matches(rawPassword, actualResponseDTO.getPassword()));
                   assertEquals(customer.getAccountNonExpired(), actualResponseDTO.getAccountNonExpired());
                   assertEquals(customer.getAccountNonLocked(), actualResponseDTO.getAccountNonLocked());
                   assertEquals(customer.getCredentialsNonExpired(), actualResponseDTO.getCredentialsNonExpired());
                   assertEquals(customer.getEnabled(), actualResponseDTO.getEnabled());
                   assertEquals(customer.getEmailAddress(), actualResponseDTO.getEmailAddress());
                   assertIterableEquals(customer.getAuthorities(), actualResponseDTO.getAuthorities());
                });
    }

}