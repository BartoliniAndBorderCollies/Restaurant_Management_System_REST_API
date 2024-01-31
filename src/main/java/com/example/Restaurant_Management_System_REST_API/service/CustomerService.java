package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService implements GenericBasicCrudOperations<CustomerDTOResponse, CustomerDTORequest, Long> {

    private CustomerRepository customerRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;
    
    @Override
    public CustomerDTOResponse create(CustomerDTORequest customerDTORequest) {
        Customer customer = modelMapper.map(customerDTORequest, Customer.class);
        customer.setPassword(passwordEncoder.encode(customerDTORequest.getPassword()));
        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerDTOResponse.class);
    }

    @Override
    public CustomerDTOResponse findById(Long id) throws NotFoundInDatabaseException {
        Customer customer = customerRepository.findById(id).orElseThrow( ()-> new NotFoundInDatabaseException(Customer.class));
        return modelMapper.map(customer, CustomerDTOResponse.class);
    }

    @Override
    public List<CustomerDTOResponse> findAll() {
        return null;
    }

    @Override
    public CustomerDTOResponse update(Long id, CustomerDTORequest customerDTORequest) throws NotFoundInDatabaseException {
        return null;
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        Customer customerToDelete = customerRepository.findById(id).orElseThrow( ()->
                new NotFoundInDatabaseException(Customer.class));
        customerRepository.delete(customerToDelete);

        return new ResponseEntity<>("Customer: " + customerToDelete.getUsername() + " has been successfully deleted!",
                HttpStatus.OK);
    }
}
