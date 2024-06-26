package com.example.Restaurant_Management_System_REST_API.rest.controller;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.TableDTO.TableReservationDTO;
import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
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
    private Customer restaurantCustomer;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ReservationRepository reservationRepository;
    private Set<Authority> restaurantClientAuthoritySet;
    private Reservation reservation;
    @Autowired
    private TableRepository tableRepository;
    private List<Table> tableList;


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
        ContactDetails customerContactDetails = new ContactDetails("customer name", "Banana street", "132", "Warsaw", "11-111",
                "987654321");
        Authority restaurantClient = new Authority(null, "ROLE_CLIENT");
        authorityRepository.save(restaurantClient);
        restaurantClientAuthoritySet = new HashSet<>();
        restaurantClientAuthoritySet.add(restaurantClient);

        restaurantCustomer = new Customer(null, LocalDateTime.now(), null, customerContactDetails, null, true, true, true,
                true, "customer@onet.pl", restaurantClientAuthoritySet);
        customerRepository.save(restaurantCustomer);
    }

    @BeforeEach
    public void prepareRestaurantTables() {
        // Create tables with initial values
        tableList = new ArrayList<>();
        for(int i =0; i<3; i++) {
            Table table = new Table(null, true, null, new ArrayList<>());
            tableList.add(table);
        }

        // Save each table in the repository
        for (Table table: tableList) {
            tableRepository.save(table);
        }
    }

    @BeforeEach
    public void prepareReservationForTests() {
        time = LocalDateTime.of(2024, 3, 18, 21, 15);
        reservation = new Reservation(null, "test case", "test", 20, time,
                tableList, restaurantCustomer);
        reservationRepository.save(reservation);
    }

    @AfterAll
    public void cleanDatabase() {
        customerRepository.deleteAll();
        authorityRepository.deleteAll();
        reservationRepository.deleteAll();
        tableRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        reservationRepository.deleteAll();
    }


    private List<TableReservationDTO> getTableReservationDTOSList() {
        return reservation.getTables().stream()
                .map(table -> modelMapper.map(table, TableReservationDTO.class))
                .collect(Collectors.toList());
    }

    @Test
    public void create_ShouldAddReservationToDbAssignCustomerAndReturnReservationDTOResponse_WhenReservationDTORequestIsGiven() {
        CustomerReservationDTO customerReservationDTO = modelMapper.map(restaurantCustomer, CustomerReservationDTO.class);

        List<TableReservationDTO> tableReservationDTOS = tableList.stream()
                .map(table -> modelMapper.map(table, TableReservationDTO.class))
                .toList();

        ReservationDTO reservationDTO = new ReservationDTO(null, "Anniversary party",
                "20 years of marriage!", 15, time, tableReservationDTOS, customerReservationDTO);

        ReservationDTO expected = new ReservationDTO(null, "Anniversary party",
                "20 years of marriage!", 15, time, tableReservationDTOS, customerReservationDTO);

        webTestClient.post()
                .uri("/api/reservation/add")
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .bodyValue(reservationDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTO.class)
                .consumeWith(response -> {
                    ReservationDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(expected.getStart(), actualResponse.getStart());
                    assertIterableEquals(expected.getTables(), actualResponse.getTables());
                    assertEquals(expected.getCustomer(), actualResponse.getCustomer());
                });
    }

    @Test
    public void findById_ShouldMapAndReturnReservationDTOResponse_WhenReservationExist() {

        List<TableReservationDTO> tableListDTO = getTableReservationDTOSList();

        webTestClient.get()
                .uri("/api/reservation/find/" + reservation.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTO.class)
                .consumeWith(response -> {
                    ReservationDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(reservation.getName(), actualResponse.getName());
                    assertEquals(reservation.getDescription(), actualResponse.getDescription());
                    assertEquals(reservation.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(reservation.getStart(), actualResponse.getStart());
                    assertIterableEquals(tableListDTO, actualResponse.getTables());

                    Customer actualCustomer = modelMapper.map(actualResponse.getCustomer(), Customer.class);
                    assertEquals(reservation.getCustomer(), actualCustomer);
                });
    }

    @Test
    public void findAll_ShouldReturnReservationDTOResponseList_WhenReservationExist() {

        List<ReservationDTO> expected = Arrays.asList(modelMapper.map(reservation, ReservationDTO.class));

        webTestClient.get()
                .uri("/api/reservation/findAll")
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReservationDTO.class)
                .consumeWith(response -> {
                    List<ReservationDTO> actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.size(), actualResponse.size());
                    assertThat(actualResponse).containsExactlyInAnyOrderElementsOf(expected);
                });
    }

    @Test
    public void update_ShouldUpdateReservationAndReturnReservationDTOResponse_WhenReservationIdAndDTOIsGiven() {

        //Creating time for update procedure
        LocalDateTime updatedTime = LocalDateTime.of(1991, 4, 17, 11, 14);

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
        CustomerReservationDTO customerReservationDTO = modelMapper.map(customerForUpdate, CustomerReservationDTO.class);

        ReservationDTO reservationDTO = new ReservationDTO(null, "Birthday",
                "10 years of struggle on planet earth", 12, updatedTime, getTableReservationDTOSList(),
                customerReservationDTO);

        ReservationDTO expected = new ReservationDTO(null, "Birthday",
                "10 years of struggle on planet earth", 12, updatedTime, getTableReservationDTOSList(),
                customerReservationDTO);

        //test itself
        webTestClient.put()
                .uri("/api/reservation/update/" + reservation.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .bodyValue(reservationDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDTO.class)
                .consumeWith(response -> {
                    ReservationDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals(expected.getName(), actualResponse.getName());
                    assertEquals(expected.getDescription(), actualResponse.getDescription());
                    assertEquals(expected.getPeopleAmount(), actualResponse.getPeopleAmount());
                    assertEquals(expected.getStart(), actualResponse.getStart());
                    assertIterableEquals(expected.getTables(), actualResponse.getTables());
                });
    }

    @Test
    public void delete_ShouldDeleteReservationAndReturnResponseDTO_WhenReservationIdIsGiven() {

        webTestClient.delete()
                .uri("/api/reservation/delete/" + reservation.getId())
                .header(HttpHeaders.AUTHORIZATION, basicAuthStaffHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseDTO.class)
                .consumeWith(response -> {
                    ResponseDTO actualResponse = response.getResponseBody();
                    assertNotNull(actualResponse);
                    assertEquals("Reservation: " + reservation.getName() + " has been successfully deleted!",
                            actualResponse.getMessage());
                    assertEquals(HttpStatus.OK, actualResponse.getStatus());

                   Optional<Reservation> shouldBeDeleted = reservationRepository.findById(reservation.getId());
                   assertTrue(shouldBeDeleted.isEmpty());
                });
    }
}