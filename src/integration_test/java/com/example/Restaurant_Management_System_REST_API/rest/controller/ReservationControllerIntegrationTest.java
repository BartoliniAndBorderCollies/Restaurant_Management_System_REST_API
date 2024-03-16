package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationRequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTOResponse;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import jakarta.persistence.Basic;
import org.junit.jupiter.api.*;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void prepareCustomer() {
        time = LocalDateTime.of(2024, 3, 18, 21, 15);
        customerContactDetails = new ContactDetails("customer name", "Banana street", "132", "Warsaw", "11-111",
                "987654321");
        Authority restaurantClient = new Authority(null, "ROLE_CLIENT");
        authorityRepository.save(restaurantClient);
        Set<Authority> restaurantClientAuthoritySet = new HashSet<>();
        restaurantClientAuthoritySet.add(restaurantClient);

        restaurantCustomer = new Customer(null, LocalDateTime.now(), null, customerContactDetails, null, true, true, true,
                true, "customer@onet.pl", restaurantClientAuthoritySet);
        customerRepository.save(restaurantCustomer);
    }
}