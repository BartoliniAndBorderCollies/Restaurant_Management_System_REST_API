package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerDetailsServiceTest {

    private CustomerDetailsService customerDetailsService;
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerDetailsService = new CustomerDetailsService(customerRepository);
    }


    @Test
    public void loadUserByUserName_ShouldThrowUsernameNotFoundException_WhenGivenEmailIsNotFound() {
        //Arrange
        String email = "This email does not exist";
        when(customerRepository.findByEmailAddress(email)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(UsernameNotFoundException.class, ()->customerDetailsService.loadUserByUsername(email));
    }

}