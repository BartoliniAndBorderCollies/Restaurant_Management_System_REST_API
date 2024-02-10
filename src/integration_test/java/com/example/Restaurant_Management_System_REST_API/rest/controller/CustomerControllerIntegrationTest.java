package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
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
    private Customer customerOwner;
    private Customer customerStaff;

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
        customerOwner = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, //here should be encoded password
                true, true, true, true, "customer@test.eu", authoritiesManagement);
        customerRepository.save(customerOwner);

        customerStaff = new Customer(null, LocalDateTime.now(), null, null, encodedPassword,
                true, true, true, true,
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
                true, "owner@test.eu", authoritiesManagement);
    }

    @AfterAll
    public void clearRolesAndCustomers() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
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

        webTestClient.get()
                .uri("/api/customer/find/" + customerStaff.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTOResponse.class)
                .consumeWith(response -> {
                    CustomerDTOResponse actualResponseDTO = response.getResponseBody();
                    assertNotNull(actualResponseDTO);
                    assertEquals(customerStaff.getId(), actualResponseDTO.getId());
                    assertEquals(customerStaff.getReservation(), actualResponseDTO.getReservation());
                    assertEquals(customerStaff.getContactDetails(), actualResponseDTO.getContactDetails());
                    assertTrue(passwordEncoder.matches(originalPassword, actualResponseDTO.getPassword()));
                    assertEquals(customerStaff.getAccountNonExpired(), actualResponseDTO.getAccountNonExpired());
                    assertEquals(customerStaff.getAccountNonLocked(), actualResponseDTO.getAccountNonLocked());
                    assertEquals(customerStaff.getCredentialsNonExpired(), actualResponseDTO.getCredentialsNonExpired());
                    assertEquals(customerStaff.getEnabled(), actualResponseDTO.getEnabled());
                    assertEquals(customerStaff.getEmailAddress(), actualResponseDTO.getEmailAddress());
                    assertIterableEquals(customerStaff.getAuthorities(), actualResponseDTO.getAuthorities());
                });
    }

    @Test
    public void findAll_ShouldReturnCustomerDTOResponseList_WhenCustomersExist() {
        List<CustomerDTOResponse> expected = new ArrayList<>();

        expected.add(modelMapper.map(customerOwner, CustomerDTOResponse.class));
        expected.add(modelMapper.map(customerStaff, CustomerDTOResponse.class));


        webTestClient.get()
                .uri("/api/customer/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderStaff)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDTOResponse.class)
                .consumeWith(response -> {
                            List<CustomerDTOResponse> actualResponse = response.getResponseBody();
                            assertNotNull(actualResponse);

                            for (int i = 0; i < actualResponse.size(); i++) {
                                CustomerDTOResponse actual = actualResponse.get(i);
                                CustomerDTOResponse exp = expected.get(i);

                                assertEquals(exp.getEnabled(), actual.getEnabled());
                                assertEquals(exp.getAuthorities(), actual.getAuthorities());
                                assertEquals(exp.getEmailAddress(), actual.getEmailAddress());
                                assertEquals(exp.getContactDetails(), actual.getContactDetails());
                                assertEquals(exp.getAccountNonExpired(), actual.getAccountNonExpired());
                                assertEquals(exp.getAccountNonLocked(), actual.getAccountNonLocked());
                                assertEquals(exp.getCredentialsNonExpired(), actual.getCredentialsNonExpired());
                                assertEquals(exp.getPassword(), actual.getPassword());
                                assertEquals(exp.getReservation(), actual.getReservation());
                            }
                        }
                );
    }

    @Test
    public void update_ShouldUpdateCustomerOnDatabaseAndReturnCustomerDTOResponse_WhenCustomerDTORequestIsGiven() {
        Customer customerToUpdate = new Customer(null, LocalDateTime.now(), null, null,
                "jajko1#", true, false, false, false,
                "jaja@test.eu", authoritiesManagement);
        customerRepository.save(customerToUpdate);

        CustomerDTORequest  customerDTORequest2 = new CustomerDTORequest(null, LocalDateTime.now(), null,
                null, "laleD3%", true, true, true,
                true, "fiku@test.eu", authoritiesManagement);

        webTestClient.put()
                .uri("/api/customer/update/" + customerToUpdate.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .bodyValue(customerDTORequest2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTOResponse.class)
                .consumeWith(response -> {
                    CustomerDTOResponse actualDTOResponse = response.getResponseBody();
                    assertNotNull(actualDTOResponse);
                    assertEquals(customerDTORequest2.getAuthorities(), actualDTOResponse.getAuthorities());
                    assertEquals(customerDTORequest2.getAccountNonLocked(), actualDTOResponse.getAccountNonLocked());
                    assertEquals(customerDTORequest2.getAccountNonExpired(), actualDTOResponse.getAccountNonExpired());
                    assertEquals(customerDTORequest2.getEnabled(), actualDTOResponse.getEnabled());
                    assertEquals(customerDTORequest2.getContactDetails(), actualDTOResponse.getContactDetails());
                    assertEquals(customerDTORequest2.getCredentialsNonExpired(), actualDTOResponse.getCredentialsNonExpired());
                    assertEquals(customerDTORequest2.getEmailAddress(), actualDTOResponse.getEmailAddress());
                    assertTrue(passwordEncoder.matches(customerDTORequest2.getPassword(), actualDTOResponse.getPassword()));
                });
        customerRepository.delete(customerToUpdate);
    }

    @Test
    public void delete_ShouldDeleteCustomerOnDatabaseAndReturnResponseEntity_WhenCustomerIdIsGiven() {
        Customer customerToDelete = new Customer(null, LocalDateTime.now(), null, null, encodedPassword, true, true, true, true,
                "staff@test.eu", authoritiesStaff);
        customerRepository.save(customerToDelete);

        webTestClient.delete()
                .uri("/api/customer/delete/" + customerToDelete.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthHeaderOwner)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseMessage = response.getResponseBody();
                    assertEquals("Customer: " + customerToDelete.getUsername() + " has been successfully deleted!",
                            responseMessage);
                    assertEquals(HttpStatus.OK, response.getStatus());
                    Optional<Customer> shouldBeEmpty = customerRepository.findById(customerToDelete.getId());
                    assertTrue(shouldBeEmpty.isEmpty());
                });
    }
}