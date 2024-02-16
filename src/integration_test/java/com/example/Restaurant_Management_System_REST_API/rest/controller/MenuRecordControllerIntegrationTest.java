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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
    @Autowired
    private ModelMapper modelMapper;

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

        MenuRecordDTORequest menuRecordDTORequest = new MenuRecordDTORequest("DTO request", "DTO description", 10.0, null,
                ingredients, Category.FOR_KIDS, true);

        webTestClient.post()
                .uri("/api/menu/record/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(menuRecordDTORequest)
                .exchange()
                .expectStatus().isCreated()
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
        MenuRecord menuRecord = new MenuRecord(ingredients, Category.SNACKS, "lovely snacks", "omommoom",
                3.0, true);
        menuRecordRepository.save(menuRecord);

        webTestClient.get()
                .uri("/api/menu/record/find/" + menuRecord.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                    MenuRecordDTOResponse menuDTOResponse = response.getResponseBody();
                    assertNotNull(menuDTOResponse);
                    assertEquals(menuRecord.getId(), menuDTOResponse.getId());
                    assertEquals(menuRecord.getName(), menuDTOResponse.getName());
                    assertEquals(menuRecord.getDescription(), menuDTOResponse.getDescription());
                    assertEquals(menuRecord.getPrice(), menuDTOResponse.getPrice());
                    assertIterableEquals(menuRecord.getIngredients(), menuDTOResponse.getIngredients());
                    assertEquals(menuRecord.getCategory(), menuDTOResponse.getCategory());
                    assertEquals(menuRecord.getIsAvailable(), menuDTOResponse.getIsAvailable());
                });
    }

    @Test
    public void findAll_ShouldReturnMenuRecordDTOResponseList_WhenMenuRecordsExist() {

        List<MenuRecord> menuRecords = Arrays.asList(
                new MenuRecord(ingredients, Category.SNACKS, "lovely snacks", "omommoom",
                        3.0, true),
                new MenuRecord(ingredients, Category.BEVERAGE, "Lovely drink", "thirsty!", 5.0,
                        true)
        );

        List<MenuRecordDTOResponse> expected = menuRecords.stream()
                .map(record -> {
                    menuRecordRepository.save(record);
                    return modelMapper.map(record, MenuRecordDTOResponse.class);
                })
                .collect(Collectors.toList());

        webTestClient.get()
                .uri("/api/menu/record/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                    List<MenuRecordDTOResponse> responseList = response.getResponseBody();
                    assertNotNull(responseList);
                    assertIterableEquals(expected, responseList);

                    for (int i = 0; i < responseList.size(); i++) {
                        MenuRecordDTOResponse actual = responseList.get(i);
                        MenuRecordDTOResponse exp = expected.get(i);

                        assertEquals(exp.getId(), actual.getId());
                        assertEquals(exp.getName(), actual.getName());
                        assertEquals(exp.getDescription(), actual.getDescription());
                        assertEquals(exp.getPrice(), actual.getPrice());
                        assertEquals(exp.getCategory(), actual.getCategory());
                        assertEquals(exp.getIngredients(), actual.getIngredients());
                        assertEquals(exp.getIsAvailable(), actual.getIsAvailable());
                    }
                });
    }

    @Test
    public void update_ShouldUpdateMenuRecordAndMapAndReturnMenuRecordDTOResponse_WhenMenuRecordDTORequestAndIdAreGiven() {

        MenuRecord menuRecordOld = new MenuRecord(ingredients, Category.SNACKS, "lovely snacks", "omommoom",
                3.0, true);
        MenuRecordDTORequest menuRecordDTORequest = new MenuRecordDTORequest("Updated name", "This is updated description",
                100.00, null, ingredients, Category.FOR_KIDS, true);

        menuRecordRepository.save(menuRecordOld);

        webTestClient.put()
                .uri("/api/menu/record/update/" + menuRecordOld.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(menuRecordDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuRecordDTOResponse.class)
                .consumeWith(response -> {
                    MenuRecordDTOResponse DTOResponse = response.getResponseBody();
                    assertNotNull(DTOResponse);
                    assertEquals(menuRecordOld.getId(), DTOResponse.getId());
                    assertEquals(menuRecordDTORequest.getName(), DTOResponse.getName());
                    assertEquals(menuRecordDTORequest.getDescription(), DTOResponse.getDescription());
                    assertEquals(menuRecordDTORequest.getPrice(), DTOResponse.getPrice());
                    assertEquals(menuRecordDTORequest.getCategory(), DTOResponse.getCategory());
                    assertIterableEquals(menuRecordDTORequest.getIngredients(), DTOResponse.getIngredients());
                    assertEquals(menuRecordDTORequest.getIsAvailable(), DTOResponse.getIsAvailable());
        });
    }

    @Test
    public void delete_ShouldDeleteMenuRecordAndReturnResponseEntity_WhenIdIsGiven() {

        MenuRecord menuRecord = new MenuRecord(ingredients, Category.SNACKS, "lovely snacks", "omommoom",
                3.0, true);
        menuRecordRepository.save(menuRecord);

        webTestClient.delete()
                .uri("/api/menu/record/delete/" + menuRecord.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = response.getResponseBody();
                    assertEquals("Menu record has been deleted!", responseBody);
                    assertEquals(HttpStatus.OK, response.getStatus());
                    Optional<MenuRecord> shouldBeEmpty = menuRecordRepository.findById(menuRecord.getId());
                    assertTrue(shouldBeEmpty.isEmpty());
                });
    }

}