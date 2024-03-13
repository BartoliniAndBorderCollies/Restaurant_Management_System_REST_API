package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Validator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    private ReservationService reservationService;
    private ReservationRepository reservationRepository;
    private ModelMapper modelMapper;
    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;
    private Validator validator;


    @BeforeEach
    public void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        modelMapper = mock(ModelMapper.class);
        authorityRepository = mock(AuthorityRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        validator = mock(Validator.class);
        customerRepository = mock(CustomerRepository.class);

        customerService = new CustomerService(customerRepository, authorityRepository, modelMapper, passwordEncoder, validator );
        reservationService = new ReservationService(reservationRepository, modelMapper, customerService);
    }


    @Test
    public void create_ShouldThrowNotFoundInDatabaseException_WhenCustomerDoesNotExist() throws NotFoundInDatabaseException {
        //Arrange
        ReservationDTORequest reservationDTORequest = mock(ReservationDTORequest.class);
        Customer customer = mock(Customer.class);
        Reservation reservation = mock(Reservation.class);

        String emailAddress = "test@example.com";


        when(modelMapper.map(reservationDTORequest, Reservation.class)).thenReturn(reservation);
        when(reservation.getCustomer()).thenReturn(customer);
        when(customerService.getCustomerFromReservationByEmailAddress(reservation)).thenReturn(customer);
        when(reservation.getCustomer()).thenReturn(customer);
        when(reservation.getCustomer().getEmailAddress()).thenReturn(emailAddress);
        when(customerRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> reservationService.create(reservationDTORequest));
    }

}