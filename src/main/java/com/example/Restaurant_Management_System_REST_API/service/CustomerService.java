package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.AuthorityRepository;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.hibernate.PropertyValueException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerService implements GenericBasicCrudOperations<CustomerDTOResponse, CustomerDTORequest, Long> {

    private final CustomerRepository customerRepository;
    private final AuthorityRepository authorityRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    
    @Override
    public CustomerDTOResponse create(CustomerDTORequest customerDTORequest) {

        // First I Validate the CustomerDTORequest
        validate(customerDTORequest);

        //then I check if request is provided with authorities -> it should be, otherwise it throws an exception
        checkIfAuthoritiesAreNull(customerDTORequest);

        // Now I fetch the authorities from the database
        // this is necessary because I don't want to have cascade in Customer, because I just want to have 4 roles in total
        Set<Authority> authorities = getAuthorities(customerDTORequest);

        //then I map to Customer and I encode the password to be hashed in database
        Customer customer = modelMapper.map(customerDTORequest, Customer.class);
        customer.setPassword(passwordEncoder.encode(customerDTORequest.getPassword()));

        customer.setAuthorities(authorities);
        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerDTOResponse.class);
    }

    private Set<Authority> getAuthorities(CustomerDTORequest customerDTORequest) {
        return customerDTORequest.getAuthorities().stream()
                .map(authorityDTO -> {
                    String authorityName = authorityDTO.getName();
                    try {
                        return authorityRepository.findByName(authorityName)
                                .orElseThrow(() -> new NotFoundInDatabaseException(Authority.class));
                    } catch (NotFoundInDatabaseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private void checkIfAuthoritiesAreNull(CustomerDTORequest customerDTORequest) {
        if(customerDTORequest.getAuthorities() == null) {
            throw new PropertyValueException("The authorities field in your request is null", "You tried to create a Customer",
                    " But the missing field is: authorities.");
        }
    }

    private void validate(CustomerDTORequest customerDTORequest) {
        Set<ConstraintViolation<CustomerDTORequest>> violations = validator.validate(customerDTORequest);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
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
        Optional.ofNullable(customerDTORequest.getAccountNonExpired()).ifPresent(customerToUpdate::setAccountNonExpired);
        Optional.ofNullable(customerDTORequest.getAccountNonLocked()).ifPresent(customerToUpdate::setAccountNonLocked);
        Optional.ofNullable(customerDTORequest.getCredentialsNonExpired()).ifPresent(customerToUpdate::setCredentialsNonExpired);
        Optional.ofNullable(customerDTORequest.getEnabled()).ifPresent(customerToUpdate::setEnabled);
        Optional.ofNullable(customerDTORequest.getEmailAddress()).ifPresent(customerToUpdate::setEmailAddress);
        Optional.ofNullable(customerDTORequest.getAuthorities()).ifPresent(customerToUpdate::setAuthorities);

        Customer customer = modelMapper.map(customerToUpdate, Customer.class);

        Optional.ofNullable(customerDTORequest.getPassword()).ifPresent(password ->
                customer.setPassword(passwordEncoder.encode(password)));

        customerRepository.save(customer);

        return modelMapper.map(customer, CustomerDTOResponse.class);
    }

    @Override
    public ResponseDTO delete(Long id) throws NotFoundInDatabaseException {
        Customer customerToDelete = customerRepository.findById(id).orElseThrow( ()->
                new NotFoundInDatabaseException(Customer.class));
        customerRepository.delete(customerToDelete);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Customer: " + customerToDelete.getUsername() + " has been successfully deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }

    Optional<Customer> getCustomerFromReservationByEmailAddress(Reservation reservation) {
        String emailAddress = reservation.getCustomer().getEmailAddress();
        return customerRepository.findByEmailAddress(emailAddress);
    }

    void checkIfCustomerHasAnyReservation(Customer customer) throws CustomerAlreadyHasReservationException {
        if (customer.getReservation() != null && customer.getReservation().getId() != null) {
            throw new CustomerAlreadyHasReservationException();
        }
    }
}
