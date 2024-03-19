package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Autowired
    private ModelMapper modelMapper;
    private ContactDetails contactDetails;
    private Supplier supplier;

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

    @BeforeEach
    public void prepareSupplier() {
        contactDetails = new ContactDetails("test name", "test street", "test houseNumber",
                "test city", "11-015", "123456789");
        supplier = new Supplier(null, contactDetails, null);
        supplierRepository.save(supplier);
    }

    @AfterEach
    public void cleanDatabase() {
        supplierRepository.deleteAll();
    }

    @AfterAll
    public void cleanCustomers() {
        customerRepository.deleteAll();
    }


    @Test
    public void add_ShouldCreateSupplierAndSaveItInDatabaseAndReturnSupplierDTOResponse_WhenSupplierDTORequestIsGiven() {
        supplierRepository.deleteAll();
        SupplierDTO supplierDTORequest = new SupplierDTO(null, contactDetails, null);
        SupplierDTO expected = new SupplierDTO(null, contactDetails, null);

        webTestClient.post()
                .uri("/api/supplier/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(supplierDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SupplierDTO.class)
                .consumeWith(response -> {
                    SupplierDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertNotNull(actualResponse.getId());
                    assertEquals(expected.getContactDetails().getName(), actualResponse.getContactDetails().getName());
                    assertEquals(expected.getContactDetails().getStreet(), actualResponse.getContactDetails().getStreet());
                    assertEquals(expected.getContactDetails().getHouseNumber(), actualResponse.getContactDetails().getHouseNumber());
                    assertEquals(expected.getContactDetails().getPostalCode(), actualResponse.getContactDetails().getPostalCode());
                    assertEquals(expected.getContactDetails().getTelephoneNumber(), actualResponse.getContactDetails().getTelephoneNumber());
                });
    }

    @Test
    public void findAll_ShouldReturnSupplierDTOResponseList_WhenSupplierExist() {

        SupplierDTO supplierDTO = modelMapper.map(supplier, SupplierDTO.class);
        supplierDTO.setInventoryItemList(new ArrayList<>()); // set inventoryItemList to an empty list otherwise
        //there is a difference in assertion (null vs empty list)

        List<SupplierDTO> expected = Arrays.asList(supplierDTO);

        webTestClient.get()
                .uri("/api/supplier/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SupplierDTO.class)
                .consumeWith(response -> {
                    List<SupplierDTO> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.size(), actualResponse.size());
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);
                });
    }

    @Test
    public void deleteById_ShouldDeleteSupplierInDbAndReturnResponseEntity_WhenSupplierIdIsGiven() {

        webTestClient.delete()
                .uri("/api/supplier/delete/" + supplier.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals("Supplier: " + supplier.getId() + " has been deleted!", actualResponse);
                    assertEquals(HttpStatus.OK, response.getStatus());
                    Optional<Supplier> optionalSupplier = supplierRepository.findById(supplier.getId());
                    assertTrue(optionalSupplier.isEmpty());
                });
    }
}