package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ModelMapper modelMapper;

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

    @AfterAll
    public void clearRolesAndCustomers() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        inventoryItemRepository.deleteAll();
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

    @Test
    public void findById_ShouldReturnInventoryItemDTOResponse_WhenInventoryItemIdExist() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2024, 2, 26, 14, 29, 20);

        InventoryItem inventoryItem = new InventoryItem(null, fixedDateTime, 55, null,
                "Pepper", "Black pepper", 0.19);
        inventoryItemRepository.save(inventoryItem);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(null, fixedDateTime, 55, null,
                "Pepper", "Black pepper", 0.19);

        webTestClient.get()
                .uri("/api/inventory/find/" + inventoryItem.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryItemDTOResponse.class)
                .consumeWith(response-> {
                    InventoryItemDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getDeliveryDate(), actualResponse.getDeliveryDate());
                    assertNotNull(actualResponse.getId());
                    assertEquals(expected.getStockAmount(), actualResponse.getStockAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
        inventoryItemRepository.deleteAll();
    }

    @Test
    public void findAll_ShouldReturnInventoryItemDTOResponseList_WhenInventoryExist() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2024, 2, 27, 9, 28);

        InventoryItem inventoryItem = new InventoryItem(null, fixedDateTime, 55, null,
                "Pepper", "Black pepper", 0.19);
        InventoryItem inventoryItem2 = new InventoryItem(null, fixedDateTime, 100, null,
                "Salt", "Sea salt", 0.49);

        inventoryItemRepository.saveAll(Arrays.asList(inventoryItem, inventoryItem2));

        List<InventoryItemDTOResponse> expected = Arrays.asList(
                modelMapper.map(inventoryItem, InventoryItemDTOResponse.class),
                modelMapper.map(inventoryItem2, InventoryItemDTOResponse.class)
        );

        webTestClient.get()
                .uri("/api/inventory/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(InventoryItemDTOResponse.class)
                .consumeWith(response -> {
                    List<InventoryItemDTOResponse> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);

                });
        inventoryItemRepository.deleteAll();
    }

    @Test
    public void update_ShouldUpdateInventoryItemAndReturnInventoryDTOResponse_WhenIdAndInventoryItemDTORequestAreGiven() {
        LocalDateTime fixedDateTime = LocalDateTime.of(2024, 2, 27, 9, 28);

        InventoryItem inventoryItem = new InventoryItem(null, fixedDateTime, 55, null,
                "Pepper", "Black pepper", 0.19);
        inventoryItemRepository.save(inventoryItem);

        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest(999L,
                LocalDateTime.of(2024, 3, 1, 10,2, 59), 1000,
                null, "Updated pepper","So nice updated pepper", 0.99);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(999L,
                LocalDateTime.of(2024, 3, 1, 10,2, 59), 1000,
                null, "Updated pepper","So nice updated pepper", 0.99);

        webTestClient.put()
                .uri("/api/inventory/update/" + inventoryItem.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .bodyValue(inventoryItemDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InventoryItemDTOResponse.class)
                .consumeWith(response -> {
                    InventoryItemDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getDeliveryDate(), actualResponse.getDeliveryDate());
                    assertEquals(expected.getStockAmount(), actualResponse.getStockAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
        inventoryItemRepository.deleteAll();
    }
}