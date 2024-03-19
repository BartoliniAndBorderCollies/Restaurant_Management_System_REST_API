package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationRequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import org.junit.jupiter.api.*;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private CustomerRepository customerRepository;
    private String basicAuthStaffHeader;
    private LocalDateTime time;
    private ContactDetails customerContactDetails;
    private Customer restaurantCustomer;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ReservationRepository reservationRepository;
    private Set<Authority> restaurantClientAuthoritySet;


    @BeforeAll
    public void prepareEnvironment() {
        reservationRepository.deleteAll();

        //Creation of application user - staff of the restaurant
        String rawPassword = "Lalka!1%";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Authority authorityStaff = new Authority(null, "ROLE_STAFF");
        authorityRepository.save(authorityStaff);

        Set<Authority> authoritySet = new HashSet<>();
        authoritySet.add(authorityStaff);

        ContactDetails contactDetails = new ContactDetails("staff", "street test", "12",
                "Poznań", "11-015", "123456789");

        Customer staff = new Customer(null, LocalDateTime.now(), null, contactDetails, encodedPassword, true,
                true, true, true, "staffik@example.eu", authoritySet);
        customerRepository.save(staff);

        //Defining basicAuthHeader required for authorization in the integration test
        basicAuthStaffHeader = "Basic " + Base64.getEncoder()
                .encodeToString((staff.getEmailAddress() + ":" + rawPassword).getBytes());
    }

    @BeforeEach
    public void prepareRestaurantClient() {
        time = LocalDateTime.of(2024, 3, 18, 21, 15);
        customerContactDetails = new ContactDetails("customer name", "Banana street", "132", "Warsaw", "11-111",
                "987654321");
        Authority restaurantClient = new Authority(null, "ROLE_CLIENT");
        authorityRepository.save(restaurantClient);
        restaurantClientAuthoritySet = new HashSet<>();
        restaurantClientAuthoritySet.add(restaurantClient);

        restaurantCustomer = new Customer(null, LocalDateTime.now(), null, customerContactDetails, null, true, true, true,
                true, "customer@onet.pl", restaurantClientAuthoritySet);
        customerRepository.save(restaurantCustomer);
    }

    @AfterAll
    public void cleanDatabase() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    @Test
    public void create_ShouldAddReservationToDbAssignCustomerAndReturnReservationDTOResponse_WhenReservationDTORequestIsGiven() {
        CustomerDTOReservationRequest customerDTOReservationRequest = modelMapper.map(restaurantCustomer, CustomerDTOReservationRequest.class);
        CustomerDTOReservationResponse customerDTOReservationResponse = modelMapper.map(restaurantCustomer, CustomerDTOReservationResponse.class);

        ReservationDTORequest reservationDTORequest = new ReservationDTORequest(null, "Anniversary party",
                "20 years of marriage!", 15, time, null, customerDTOReservationRequest);

        ReservationDTOResponse expected = new ReservationDTOResponse(null, "Anniversary party",
                "20 years of marriage!", 15, time, null, customerDTOReservationResponse);

        webTestClient.post()
                .uri("/api/reservation/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .bodyValue(reservationDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTOResponse.class)
                .consumeWith(response -> {
                    ReservationDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(expected.getStart(), actualResponse.getStart());
                    assertIterableEquals(expected.getTables(), actualResponse.getTables());
                    assertEquals(expected.getCustomer(), actualResponse.getCustomer());
                });
        reservationRepository.deleteAll();
    }

    @Test
    public void findById_ShouldMapAndReturnReservationDTOResponse_WhenReservationExist() {
        LocalDateTime time = LocalDateTime.of(1990, 3, 18, 10, 11);
        Reservation reservation = new Reservation(null, "test case", "test", 20, time,
                null, restaurantCustomer);
        reservationRepository.save(reservation);

        webTestClient.get()
                .uri("/api/reservation/find/" + reservation.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTOResponse.class)
                .consumeWith(response -> {
                    ReservationDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(reservation.getName(), actualResponse.getName());
                    assertEquals(reservation.getDescription(), actualResponse.getDescription());
                    assertEquals(reservation.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(reservation.getStart(), actualResponse.getStart());
                    assertTrue(actualResponse.getTables().isEmpty());
                    Customer actualCustomer = modelMapper.map(actualResponse.getCustomer(), Customer.class);
                    assertEquals(reservation.getCustomer(), actualCustomer);
                });
        reservationRepository.deleteAll();
    }

    @Test
    public void findAll_ShouldReturnReservationDTOResponseList_WhenReservationExist() {
        LocalDateTime time = LocalDateTime.of(1990, 3, 18, 10, 11);
        Reservation reservation = new Reservation(null, "test case", "test", 20, time,
                null, restaurantCustomer);
        reservation.setTables(new ArrayList<>()); //I set this as empty list, otherwise I got assertion failure null vs empty
        reservationRepository.save(reservation);

        List<ReservationDTOResponse> expected = Arrays.asList(modelMapper.map(reservation, ReservationDTOResponse.class));

        webTestClient.get()
                .uri("/api/reservation/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationDTOResponse.class)
                .consumeWith(response -> {
                    List<ReservationDTOResponse> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.size(), actualResponse.size());
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);
                });
        reservationRepository.deleteAll();
    }

    @Test
    public void update_ShouldUpdateReservationAndReturnReservationDTOResponse_WhenReservationIdAndDTOIsGiven() {

        //Creating times
        LocalDateTime originalTime = LocalDateTime.of(1987, 1, 1, 3, 18);
        LocalDateTime updatedTime = LocalDateTime.of(1991, 4, 17, 11, 14);

        //Creating reservation which is going to be updated and saving it to repo
        Reservation originalReservation = new Reservation(null, "test case2", "test2", 9,
                originalTime,null, restaurantCustomer);
        reservationRepository.save(originalReservation);

        //Creating a new customer to whom updated reservation will be set
        ContactDetails contactDetailsForUpdate = new ContactDetails("update name", "update street",
                "11", "Lodz", "00-111", "123456788");
        String rawPasswordForUpdate = "Kkl1@$1j";
        String encodedPasswordForUpdate = passwordEncoder.encode(rawPasswordForUpdate);

        Customer customerForUpdate = new Customer(null, updatedTime, null, contactDetailsForUpdate,
                encodedPasswordForUpdate,true, true, true, true,
                "exampleForUpdate@onet.pl", restaurantClientAuthoritySet);
        customerRepository.save(customerForUpdate);

        //Mapping created customer to DTOs to be able to provide it to request and to check in response
        CustomerDTOReservationRequest customerDTORequest = modelMapper.map(customerForUpdate, CustomerDTOReservationRequest.class);
        CustomerDTOReservationResponse customerDTOResponse = modelMapper.map(customerForUpdate, CustomerDTOReservationResponse.class);

        ReservationDTORequest reservationDTORequest = new ReservationDTORequest(null, "Birthday",
                "10 years of struggle on planet earth", 12, updatedTime, null, customerDTORequest);

        ReservationDTOResponse expected = new ReservationDTOResponse(null, "Birthday",
                "10 years of struggle on planet earth", 12, updatedTime, null, customerDTOResponse);

        //test itself
        webTestClient.put()
                .uri("/api/reservation/update/" + originalReservation.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .bodyValue(reservationDTORequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTOResponse.class)
                .consumeWith(response -> {
                    ReservationDTOResponse actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(expected.getStart(), actualResponse.getStart());
                    assertTrue(actualResponse.getTables().isEmpty());
                    assertEquals(expected.getCustomer(), actualResponse.getCustomer());
                });
        reservationRepository.deleteAll();
    }

}