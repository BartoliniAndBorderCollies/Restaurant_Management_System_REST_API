package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import com.example.Restaurant_Management_System_REST_API.repository.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private ReservationRepository reservationRepository;
    private ModelMapper modelMapper;
    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;
    private Validator validator;
    private ReservationDTO reservationDTO;
    private Customer customer;
    private Reservation reservation;
    private TableService tableService;
    private TableRepository tableRepository;


    @BeforeEach
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        modelMapper = mock(ModelMapper.class);
        authorityRepository = mock(AuthorityRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        validator = mock(Validator.class);
        customerRepository = mock(CustomerRepository.class);
        tableRepository = mock(TableRepository.class);

        tableService = new TableService(tableRepository, modelMapper);
        customerService = new CustomerService(customerRepository, authorityRepository, modelMapper, passwordEncoder, validator);
        reservationService = new ReservationService(reservationRepository, modelMapper, customerService, tableService);
    }

    @BeforeEach
    public void prepareMocksForTestMethods() {
        reservationDTO = mock(ReservationDTO.class);
        customer = mock(Customer.class);
        reservation = mock(Reservation.class);
    }


    @Test
    public void create_ShouldThrowNotFoundInDatabaseException_WhenCustomerDoesNotExist() {
        //Arrange

        when(modelMapper.map(reservationDTO, Reservation.class)).thenReturn(reservation);
        when(reservation.getCustomer()).thenReturn(customer);
        when(customerService.getCustomerFromReservationByEmailAddress(reservation)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> reservationService.create(reservationDTO));
    }

    @Test
    public void create_ShouldThrowCustomerAlreadyHasReservationException_WhenReservationIdIsAssignedToCustomer() {
        //Arrange

        when(modelMapper.map(reservationDTO, Reservation.class)).thenReturn(reservation);
        when(reservation.getCustomer()).thenReturn(customer);
        when(customerService.getCustomerFromReservationByEmailAddress(reservation)).thenReturn(Optional.ofNullable(customer));
        assert customer != null;
        when(customer.getReservation()).thenReturn(reservation);
        when(customer.getReservation().getId()).thenReturn(1L);

        //Act
        //Assert
        assertThrows(CustomerAlreadyHasReservationException.class, () -> reservationService.create(reservationDTO));
    }

    @Test
    public void create_ShouldCallOnReservationRepositoryExactlyOnce_WhenReservationDTORequestIsGiven()
            throws CustomerAlreadyHasReservationException, NotFoundInDatabaseException {
        //Arrange

        when(modelMapper.map(reservationDTO, Reservation.class)).thenReturn(reservation);
        when(reservation.getCustomer()).thenReturn(customer);
        when(customerService.getCustomerFromReservationByEmailAddress(reservation)).thenReturn(Optional.ofNullable(customer));
        assert customer != null;
        when(customer.getReservation()).thenReturn(reservation);
        when(customer.getReservation().getId()).thenReturn(null);
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        //Act
        reservationService.create(reservationDTO);

        //Assert
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenReservationDoesNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> reservationService.findById(nonExistedId));
    }

    @Test
    public void findById_ShouldReturnReservationDTOResponse_WhenReservationIdIsGivenAndReservationExists()
            throws NotFoundInDatabaseException {
        //Arrange
        Long id = 1L;
        LocalDateTime time = LocalDateTime.of(2020, 8, 10, 10, 15);
        ReservationDTO expected = new ReservationDTO(id, "expected", "nice",
                10, time, null, null);
        Reservation reservation = new Reservation(id, "expected", "nice",
                10, time, null, null);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(modelMapper.map(reservation, ReservationDTO.class)).thenReturn(expected);

        //Act
        ReservationDTO actual = reservationService.findById(id);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void findAll_ShouldReturnReservationDTOResponseList_WhenReservationExist() {
        //Arrange
        Long id = 1L;
        LocalDateTime time = LocalDateTime.of(2020, 8, 10, 10, 15);
        ReservationDTO reservationDTO = new ReservationDTO(id, "expected", "nice",
                10, time, null, null);
        Reservation reservation = new Reservation(id, "expected", "nice",
                10, time, null, null);

        List<ReservationDTO> expectedList = Arrays.asList(reservationDTO);
        List<Reservation> reservationList = Arrays.asList(reservation);

        when(reservationRepository.findAll()).thenReturn(reservationList);
        when(modelMapper.map(reservation, ReservationDTO.class)).thenReturn(reservationDTO);

        //Act
        List<ReservationDTO> actual = reservationService.findAll();

        //Assert
        assertIterableEquals(expectedList, actual);
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenGivenReservationIdDoesNotExist() {
        //Arrange
        Long nonExistedId = 999L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> reservationService.update(nonExistedId, reservationDTO));
    }

    @Test
    public void update_ShouldFindMapGetFindMapAgainAndReturnDTO_WhenReservationIdAndDTORequestAreGiven()
            throws NotFoundInDatabaseException {
        //Arrange
        Long id = 1L;
        LocalDateTime time = LocalDateTime.of(2020, 8, 10, 10, 15);
        CustomerReservationDTO customerDTO = mock(CustomerReservationDTO.class);
        CustomerReservationDTO customerDTOResponse = mock(CustomerReservationDTO.class);
        String email = "example@example.eu";

        ReservationDTO expected = new ReservationDTO(id, "expected", "nice",
                10, time, null, customerDTOResponse);
        ReservationDTO reservationDTO = new ReservationDTO(id, "expected", "nice",
                10, time, null, customerDTO);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
        when(modelMapper.map(reservationDTO, Reservation.class)).thenReturn(reservation);
        when(reservation.getCustomer()).thenReturn(customer);
        when(reservation.getCustomer().getEmailAddress()).thenReturn(email);
        when(customerRepository.findByEmailAddress(email)).thenReturn(Optional.of(customer));
        when(modelMapper.map(reservation, ReservationDTO.class)).thenReturn(expected);

        //Act
        ReservationDTO actual = reservationService.update(id, reservationDTO);

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void delete_ShouldThrowNotFoundInDatabaseException_WhenGivenIdDoesNotExist() {
        //Arrange
        Long nonExistedId = 888L;

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, ()-> reservationService.delete(nonExistedId));
    }

    @Test
    public void delete_ShouldCallOnReservationRepoExactlyOnce_WhenCorrectIdIsGiven() throws NotFoundInDatabaseException {
        //Arrange
        Long id = 1L;
        LocalDateTime time = LocalDateTime.of(2024, 3, 14, 11, 36);
        Reservation reservation = new Reservation(id, "expected", "nice",
                10, time, null, null);

        when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

        //Act
        reservationService.delete(reservation.getId());

        //Assert
        verify(reservationRepository, times(1)).delete(reservation);
    }

}