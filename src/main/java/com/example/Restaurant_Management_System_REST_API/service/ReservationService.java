package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerDTOReservationResponse;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class ReservationService implements GenericBasicCrudOperations<ReservationDTOResponse, ReservationDTORequest, Long> {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Override
    public ReservationDTOResponse create(ReservationDTORequest reservationDTORequest) throws NotFoundInDatabaseException,
            CustomerAlreadyHasReservationException {
        Reservation reservation = modelMapper.map(reservationDTORequest, Reservation.class);

        assignCustomerToReservationAndSave(reservation);

        return modelMapper.map(reservation, ReservationDTOResponse.class);
    }

    private void assignCustomerToReservationAndSave(Reservation reservation) throws NotFoundInDatabaseException,
            CustomerAlreadyHasReservationException {

        //checking if this customer exists and getting him if exists
        if (reservation.getCustomer() != null) {
            Customer customer = getCustomerFromReservationByEmailAddress(reservation);

            //checking if this customer already has any reservation (customer cannot have more than one reservation at all)
            checkIfCustomerHasAnyReservation(customer);

            reservation.setCustomer(customer);
        }

        reservationRepository.save(reservation);
    }

    private Customer getCustomerFromReservationByEmailAddress(Reservation reservation) throws NotFoundInDatabaseException {
        String emailAddress = reservation.getCustomer().getEmailAddress();
        return customerRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new NotFoundInDatabaseException(Customer.class));
    }

    private void checkIfCustomerHasAnyReservation(Customer customer) throws CustomerAlreadyHasReservationException {
        if (customer.getReservation() != null && customer.getReservation().getId() != null) {
            throw new CustomerAlreadyHasReservationException();
        }
    }

    @Override
    public ReservationDTOResponse findById(Long id) throws NotFoundInDatabaseException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(Reservation.class));

        return modelMapper.map(reservation, ReservationDTOResponse.class);
    }

    @Override
    public List<ReservationDTOResponse> findAll() {
        List<ReservationDTOResponse> reservationDTOResponseList = new ArrayList<>();

        reservationRepository.findAll().forEach(reservation ->
                reservationDTOResponseList.add(modelMapper.map(reservation, ReservationDTOResponse.class)));

        return reservationDTOResponseList;
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        Reservation reservationToDelete = reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(Reservation.class));
        reservationRepository.delete(reservationToDelete);

        return new ResponseEntity<>("Reservation: " + reservationToDelete.getName() + " has been successfully deleted!",
                HttpStatus.OK);
    }

}
