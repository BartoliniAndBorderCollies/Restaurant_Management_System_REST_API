package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTOResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.InventoryItemRepository;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
    @Autowired
    private SupplierRepository supplierRepository;
    private Supplier supplier;

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

    @BeforeAll
    public void createSupplier() {
        ContactDetails contactDetails = new ContactDetails("Test supplier", "Test street", "Test house number", "Test city",
                "00-000", "123456789");
        supplier = new Supplier(null, contactDetails, new ArrayList<>());
        supplierRepository.save(supplier);
    }

    @AfterAll
    public void clearRolesAndCustomers() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        supplierRepository.deleteAll();
    }

    @Test
    public void create_ShouldCreateInventoryItemInDbAndReturnDTOResponse_WhenInventoryItemDTORequestIsGiven() {

        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest(null, null,
                100, supplier, "Potatoes", "Potatoes", 1.49);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(null, 100,
                supplier, "Potatoes", "Potatoes", 1.49 );

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
                    assertEquals(expected.getAmount(), actualResponse.getAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
    }

    @Test
    public void findById_ShouldReturnInventoryItemDTOResponse_WhenInventoryItemIdExist() {

        InventoryItem inventoryItem = new InventoryItem(null, 55, supplier,
                "Pepper", "Black pepper", 0.19);
        inventoryItemRepository.save(inventoryItem);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(null, 55, supplier,
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
                    assertNotNull(actualResponse.getId());
                    assertEquals(expected.getAmount(), actualResponse.getAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
        inventoryItemRepository.deleteAll();
    }

    @Test
    public void findAll_ShouldReturnInventoryItemDTOResponseList_WhenInventoryExist() {
        InventoryItem inventoryItem = new InventoryItem(null, 55, supplier,
                "Pepper", "Black pepper", 0.19);
        InventoryItem inventoryItem2 = new InventoryItem(null, 100, supplier,
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
        InventoryItem inventoryItem = new InventoryItem(null, 55, supplier,
                "Pepper", "Black pepper", 0.19);
        inventoryItemRepository.save(inventoryItem);

        InventoryItemDTORequest inventoryItemDTORequest = new InventoryItemDTORequest(999L,
                LocalDateTime.of(2024, 3, 1, 10,2, 59), 1000,
                null, "Updated pepper","So nice updated pepper", 0.99);

        InventoryItemDTOResponse expected = new InventoryItemDTOResponse(999L, 1000,
                supplier, "Updated pepper","So nice updated pepper", 0.99);

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
                    assertEquals(expected.getAmount(), actualResponse.getAmount());
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPrice(), actualResponse.getPrice());
                });
        inventoryItemRepository.deleteAll();
    }

    @Test
    public void delete_ShouldDeleteInventoryItemAndReturnResponseDTO_WhenIdIsGiven() {

        InventoryItem inventoryItem = new InventoryItem(null, 55, supplier,
                "Pepper", "Black pepper", 0.19);
        inventoryItemRepository.save(inventoryItem);

        webTestClient.delete()
                .uri("/api/inventory/delete/" + inventoryItem.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDTO.class)
                .consumeWith(response -> {
                    ResponseDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals("Inventory item: " + inventoryItem.getName() + " has been deleted!", actualResponse.getMessage());
                    assertEquals(HttpStatus.OK, actualResponse.getStatus());
                    Optional<InventoryItem> optionalInventoryItem = inventoryItemRepository.findById(inventoryItem.getId());
                    assertTrue(optionalInventoryItem.isEmpty());
                });
    }
}