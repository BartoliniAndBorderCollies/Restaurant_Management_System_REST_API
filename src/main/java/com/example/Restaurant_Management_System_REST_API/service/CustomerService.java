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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Iterable<Customer> customers = customerRepository.findAll();
        List<CustomerDTOResponse> customerDTOList = new ArrayList<>();

        for (Customer customer: customers) {
            customerDTOList.add(modelMapper.map(customer, CustomerDTOResponse.class));
        }

        return customerDTOList;
    }

    @Override
    public CustomerDTOResponse update(Long id, CustomerDTORequest customerDTORequest) throws NotFoundInDatabaseException {
        CustomerDTOResponse customerToUpdate = findById(id);

        Optional.ofNullable(customerDTORequest.getCreationTime()).ifPresent(customerToUpdate::setCreationTime);
        Optional.ofNullable(customerDTORequest.getReservation()).ifPresent(customerToUpdate::setReservation);
        Optional.ofNullable(customerDTORequest.getContactDetails()).ifPresent(customerToUpdate::setContactDetails);
        Optional.ofNullable(customerDTORequest.getPassword()).ifPresent(password ->
                customerToUpdate.setPassword(passwordEncoder.encode(password)));
        Optional.ofNullable(customerDTORequest.getAccountNonExpired()).ifPresent(customerToUpdate::setAccountNonExpired);
        Optional.ofNullable(customerDTORequest.getAccountNonLocked()).ifPresent(customerToUpdate::setAccountNonLocked);
        Optional.ofNullable(customerDTORequest.getCredentialsNonExpired()).ifPresent(customerToUpdate::setCredentialsNonExpired);
        Optional.ofNullable(customerDTORequest.getEnabled()).ifPresent(customerToUpdate::setEnabled);
        Optional.ofNullable(customerDTORequest.getEmailAddress()).ifPresent(customerToUpdate::setEmailAddress);
        Optional.ofNullable(customerDTORequest.getAuthorities()).ifPresent(customerToUpdate::setAuthorities);

        customerRepository.save(modelMapper.map(customerToUpdate, Customer.class));

        return customerToUpdate;
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
