package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SupplierControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicAuthHeaderOwner;
    @Autowired
    private SupplierRepository supplierRepository;

    @BeforeAll
    public void setUpRolesAndCustomers() {
        String rawPassword = "Lala!5";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        //Creation of authorities and saving it to database
        Authority authorityOwner = new Authority(null, "ROLE_OWNER");
        authorityRepository.save(authorityOwner);

        //Creation of Sets and adding authorities to Sets
        Set<Authority> authorityList = new HashSet<>();
        authorityList.add(authorityOwner);

        //Creation of customers and saving it to database
        Customer owner = new Customer(null, null, null, null, encodedPassword,
                true, true, true, true, "owner@customer.eu",
                authorityList);
        customerRepository.save(owner);

        //Defining basicAuthHeader required for authorization in the integration test
        basicAuthHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((owner.getEmailAddress() + ":" + rawPassword).getBytes()); // here I need to provide a raw password
    }


    @Test
    public void add_ShouldCreateSupplierAndSaveItInDatabaseAndReturnSupplierDTOResponse_WhenSupplierDTORequestIsGiven() {
        ContactDetails contactDetails = new ContactDetails("test name", "test street", "test houseNumber",
                "test city", "test postalCode", "test telephoneNumber");

        SupplierDTORequest supplierDTORequest = new SupplierDTORequest(null, contactDetails, null);
        SupplierDTOResponse expected = new SupplierDTOResponse(null, contactDetails, null);

        webTestClient.post()
                .uri("/api/supplier/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(supplierDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SupplierDTOResponse.class)
                .consumeWith(response -> {
                    SupplierDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertNotNull(actualResponse.getId());
                    assertEquals(expected.getContactDetails().getName(), actualResponse.getContactDetails().getName());
                    assertEquals(expected.getContactDetails().getStreet(), actualResponse.getContactDetails().getStreet());
                    assertEquals(expected.getContactDetails().getHouseNumber(), actualResponse.getContactDetails().getHouseNumber());
                    assertEquals(expected.getContactDetails().getPostalCode(), actualResponse.getContactDetails().getPostalCode());
                    assertEquals(expected.getContactDetails().getTelephoneNumber(), actualResponse.getContactDetails().getTelephoneNumber());
                });
        supplierRepository.deleteAll();
    }

}