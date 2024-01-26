package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) //JUnit will create only one instance of the test class
// (this is called per-class test instance lifecycle), and the @BeforeAll method doesn’t need to be static.
class MenuRecordControllerIntegrationTest {

    private Set<String> ingredients;
    @Autowired
    private WebTestClient webTestClient;
    private String basicAuthHeaderOwner;
    private String basicAuthHeaderStaff;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MenuRecordRepository menuRecordRepository;

    @BeforeAll
    public void setUpRolesAuthoritiesAndCustomers() {

        String originalPassword = "lala";
        String encodedPassword = passwordEncoder.encode(originalPassword); //I use PasswordEncoder in SecurityConfig therefore it expects encoded password

        //Creation of different authorities and saving it to database
        Authority authorityOwner = new Authority(null, "ROLE_OWNER");
        Authority authorityStaff = new Authority(null, "ROLE_STAFF");
        authorityRepository.save(authorityOwner);
        authorityRepository.save(authorityStaff);

        //Creation of Sets and adding authorities to Sets
        Set<Authority> authoritiesManagement = new HashSet<>();
        Set<Authority> authoritiesStaff = new HashSet<>();
        authoritiesManagement.add(authorityOwner);
        authoritiesStaff.add(authorityStaff);

        //Creation of customers and saving it to database
        Customer owner = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true, "owner@wp.pl",
                authoritiesManagement);
        Customer staff = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true, "staff@wp.pl",
                authoritiesStaff);
        customerRepository.save(owner);
        customerRepository.save(staff);

        //Defining basicAuthHeader required for authorization in the integration test
        basicAuthHeaderOwner = "Basic " + Base64.getEncoder()
                .encodeToString((owner.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password

        basicAuthHeaderStaff = "Basic " + Base64.getEncoder()
                .encodeToString((staff.getEmailAddress() + ":" + originalPassword).getBytes());// here I need to provide a raw password
    }

    @BeforeEach
    public void setUp() {
        ingredients = new HashSet<>();
        ingredients.add("water");
        ingredients.add("hops");
        ingredients.add("barley");
    }

    @AfterEach
    public void clearDatabase() {
        menuRecordRepository.deleteAll();
    }

    @Test
    public void create_ShouldAddMenuRecordToDatabaseAndReturnDTOResponse_WhenDTORequestIsGiven() {

        MenuRecordDTORequest menuRecordDTORequest = new MenuRecordDTORequest(1L, ingredients, Category.BEVERAGE, true);
        //I don't have constructor for CatalogItem (DTORequest extends it) therefore I add the required fields below as follows:
        menuRecordDTORequest.setName("Beverage Name");
        menuRecordDTORequest.setDescription("Beverage Description");
        menuRecordDTORequest.setPrice(10.0);

        webTestClient.post()
                .uri("/api/menu/record/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(menuRecordDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                            MenuRecordDTOResponse menuDTOResponse = response.getResponseBody();
                            assertNotNull(menuDTOResponse);
                            assertIterableEquals(menuRecordDTORequest.getIngredients(), menuDTOResponse.getIngredients());
                            assertEquals(menuRecordDTORequest.getCategory(), menuDTOResponse.getCategory());
                            assertEquals(menuRecordDTORequest.getIsAvailable(), menuDTOResponse.getIsAvailable());
                            assertEquals(menuRecordDTORequest.getName(), menuDTOResponse.getName());
                            assertEquals(menuRecordDTORequest.getDescription(), menuDTOResponse.getDescription());
                            assertEquals(menuRecordDTORequest.getPrice(), menuDTOResponse.getPrice());
                        }
                );
    }


    @Test
    public void findById_ShouldReturnMenuRecordDTOResponse_WhenMenuRecordIdIsGiven() {
        MenuRecord menuRecord = new MenuRecord(null, ingredients, Category.SNACKS, true);
        menuRecord.setName("Lovely snacks");
        menuRecord.setDescription("omnomomomom");
        menuRecord.setPrice(3.0);
        menuRecordRepository.save(menuRecord);


        webTestClient.get()
                .uri("/api/menu/record/" + menuRecord.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                    MenuRecordDTOResponse menuDTOResponse = response.getResponseBody();
                    assertNotNull(menuDTOResponse);
                    assertEquals(menuRecord.getName(), menuDTOResponse.getName());
                    assertEquals(menuRecord.getDescription(), menuDTOResponse.getDescription());
                    assertEquals(menuRecord.getPrice(), menuDTOResponse.getPrice());
                    assertIterableEquals(menuRecord.getIngredients(), menuDTOResponse.getIngredients());
                    assertEquals(menuRecord.getCategory(), menuDTOResponse.getCategory());
                    assertEquals(menuRecord.getIsAvailable(), menuDTOResponse.getIsAvailable());
                });
    }

}