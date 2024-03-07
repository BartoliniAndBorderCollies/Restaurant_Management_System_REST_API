package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.CustomerRepository;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class ReservationService implements GenericBasicCrudOperations<ReservationDTOResponse, ReservationDTORequest, Long> {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Override
    public ReservationDTOResponse create(ReservationDTORequest reservationDTORequest) throws NotFoundInDatabaseException {
        Reservation reservation = modelMapper.map(reservationDTORequest, Reservation.class);
        setCustomerToReservation(reservation);

        return modelMapper.map(reservation, ReservationDTOResponse.class);
    }

    private void setCustomerToReservation(Reservation reservation) throws NotFoundInDatabaseException {

        if (reservation.getCustomer() != null) {
            String emailAddress = reservation.getCustomer().getEmailAddress();
            Customer customer = customerRepository.findByEmailAddress(emailAddress)
                    .orElseThrow(() -> new NotFoundInDatabaseException(Customer.class));

            reservation.setCustomer(customer);
            reservationRepository.save(reservation);
        }
    }

}
