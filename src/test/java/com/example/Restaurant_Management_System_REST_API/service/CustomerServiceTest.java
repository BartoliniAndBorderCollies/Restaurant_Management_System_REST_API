package com.example.Restaurant_Management_System_REST_API.service;


import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


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
        Exception exception = assertThrows(PropertyValueException.class, ()-> customerService.create(customerDTORequest));

        //Assert
        assertTrue(exception.getMessage().contains(expectedExceptionMessage));
    }
}