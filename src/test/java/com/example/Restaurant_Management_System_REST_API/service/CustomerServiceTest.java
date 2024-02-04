package com.example.Restaurant_Management_System_REST_API.service;


import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.validation.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private AuthorityRepository authorityRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    private Validator validator;
    private CustomerDTORequest customerDTORequest;

    @BeforeEach
    public void setUp() {
        customerRepository = mock(CustomerRepository.class);
        authorityRepository = mock(AuthorityRepository.class);
        modelMapper = mock(ModelMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
//        validator = mock(Validator.class);
        customerService = new CustomerService(customerRepository, authorityRepository, modelMapper, passwordEncoder,
                (jakarta.validation.Validator) validator);

        customerDTORequest = mock(CustomerDTORequest.class);
    }

//    @Test
//    public void create_ShouldThrowConstraintViolationException_WhenViolationSetIsNotEmpty() {
//        //Arrange
//        ConstraintViolation<CustomerDTORequest> violation = mock(ConstraintViolation.class);
//        Set<ConstraintViolation<CustomerDTORequest>> violations = new HashSet<>();
//        violations.add(violation);
//        when(validator.validate(customerDTORequest)).thenReturn(violations);
//
//        //Act
//        //Assert
//        assertThrows(ConstraintViolationException.class, ()-> customerService.create(customerDTORequest));
//    }

    @Test
    public void create_ShouldThrowPropertyValueException_WhenAuthoritiesAreNull() {
        //Arrange
        Customer customer = mock(Customer.class);
        String expectedExceptionMessage = "The authorities field in your request is null";

        when(modelMapper.map(customerDTORequest, Customer.class)).thenReturn(customer);
        when(customerDTORequest.getAuthorities()).thenReturn(null);

        //Act
        Exception exception = assertThrows(PropertyValueException.class, () -> customerService.create(customerDTORequest));

        //Assert
        assertTrue(exception.getMessage().contains(expectedExceptionMessage));
    }

    @Test
    public void create_ShouldStreamAndMapAndFindAndReturnAuthoritySet_WhenAuthoritiesExist() {
        //Arrange
        Set<Authority> expected = new HashSet<>();
        Authority authority = new Authority(1L, "ROLE_OWNER");
        expected.add(authority);

        Customer customer = new Customer(1L, LocalDateTime.now(), null, null, "lala",
                true, true, true, true, "customer@wp.pl",
                expected);
        CustomerDTOResponse customerDTOResponse = new CustomerDTOResponse();
        CustomerDTORequest customerDTORequest1 = new CustomerDTORequest();

        customerDTORequest1.setAuthorities(expected);
        customerDTOResponse.setAuthorities(expected);

        when(modelMapper.map(customerDTORequest1, Customer.class)).thenReturn(customer);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authorityRepository.findByName(authority.getName())).thenReturn(Optional.of(authority));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDTOResponse.class)).thenReturn(customerDTOResponse);

        //Act
        CustomerDTOResponse customerDTOResponseActual = customerService.create(customerDTORequest1);

        //Assert
        assertIterableEquals(expected, customerDTOResponseActual.getAuthorities());
    }

    @Test
    public void findById_ShouldThrowNotFoundInDatabaseException_WhenCustomerIdIsNotFound() {
        //Arrange
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> customerService.findById(1L));
    }

    @Test
    public void findAll_ShouldReturnCustomerDTOResponseList_WhenCustomersExist() {
        //Arrange
        Customer customer = new Customer();
        Customer customer2 = new Customer();
        Iterable<Customer> iterable = Arrays.asList(customer, customer2);

        CustomerDTOResponse customerDTOResponse1 = new CustomerDTOResponse();
        CustomerDTOResponse customerDTOResponse2 = new CustomerDTOResponse();

        List<CustomerDTOResponse> expected = Arrays.asList(customerDTOResponse1, customerDTOResponse2);

        when(customerRepository.findAll()).thenReturn(iterable);
        when(modelMapper.map(customer, CustomerDTOResponse.class)).thenReturn(customerDTOResponse1);
        when(modelMapper.map(customer2, CustomerDTOResponse.class)).thenReturn(customerDTOResponse2);

        //Act
         List<CustomerDTOResponse> actual = customerService.findAll();

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    public void update_ShouldThrowNotFoundInDatabaseException_WhenCustomerIdIsNotFound() throws NotFoundInDatabaseException {
        //Arrange
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(NotFoundInDatabaseException.class, () -> customerService.update(1L, customerDTORequest));
    }
}