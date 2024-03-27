package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs.CustomerReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
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
public class ReservationService implements GenericBasicCrudOperations<ReservationDTO, ReservationDTO,  Long> {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final CustomerService customerService;

    @Override
    public ReservationDTO create(ReservationDTO reservationDTO) throws NotFoundInDatabaseException,
            CustomerAlreadyHasReservationException {
        Reservation reservation = modelMapper.map(reservationDTO, Reservation.class);

        assignCustomerToReservationAndSave(reservation);

        return modelMapper.map(reservation, ReservationDTO.class);
    }

    private void assignCustomerToReservationAndSave(Reservation reservation) throws NotFoundInDatabaseException,
            CustomerAlreadyHasReservationException {

        //checking if this customer exists and getting him if exists
        if (reservation.getCustomer() != null) {
            Customer customer = customerService.getCustomerFromReservationByEmailAddress(reservation).orElseThrow(
                    () -> new NotFoundInDatabaseException(Customer.class));

            //checking if this customer already has any reservation (customer cannot have more than one reservation at all)
            customerService.checkIfCustomerHasAnyReservation(customer);

            reservation.setCustomer(customer);
        }

        reservationRepository.save(reservation);
    }

    @Override
    public ReservationDTO findById(Long id) throws NotFoundInDatabaseException {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(Reservation.class));

        return modelMapper.map(reservation, ReservationDTO.class);
    }

    @Override
    public List<ReservationDTO> findAll() {
        List<ReservationDTO> reservationDTOList = new ArrayList<>();

        reservationRepository.findAll().forEach(reservation ->
                reservationDTOList.add(modelMapper.map(reservation, ReservationDTO.class)));

        return reservationDTOList;
    }

    @Override
    public ReservationDTO update(Long id, ReservationDTO reservationDTORequest)
            throws NotFoundInDatabaseException {
        ReservationDTO reservationDTOToBeUpdated = findById(id);

        Optional.ofNullable(reservationDTORequest.getName()).ifPresent(reservationDTOToBeUpdated::setName);
        Optional.ofNullable(reservationDTORequest.getDescription()).ifPresent(reservationDTOToBeUpdated::setDescription);
        Optional.of(reservationDTORequest.getPeopleAmount()).ifPresent(reservationDTOToBeUpdated::setPeopleAmount);
        Optional.ofNullable(reservationDTORequest.getStart()).ifPresent(reservationDTOToBeUpdated::setStart);
        Optional.ofNullable(reservationDTORequest.getTables()).ifPresent(reservationDTOToBeUpdated::setTables);
        //Because I have different types in field of customer (CustomerReservationDTO and CustomerDTOReservationRequest)
        //I do like below. I need to use lambda because the ifPresent method expects a Consumer (a lambda that does not return a value).
        //This way, I'm passing a Consumer lambda to the ifPresent method
        Optional.ofNullable(reservationDTORequest.getCustomer()).ifPresent(customerRequest -> {
            try {
                //checking if customer exists
                Customer customerFromRequest = customerService.getCustomerFromReservationByEmailAddress
                        (modelMapper.map(reservationDTORequest, Reservation.class)).orElseThrow(() -> new NotFoundInDatabaseException(Customer.class));

                //checking if this customer already has any reservation
                customerService.checkIfCustomerHasAnyReservation(customerFromRequest);

                //setting new customer to the reservation
                reservationDTOToBeUpdated.setCustomer(modelMapper.map(customerFromRequest, CustomerReservationDTO.class));

            } catch (NotFoundInDatabaseException | CustomerAlreadyHasReservationException e) {
                //orElseThrow method can potentially throw a NotFoundInDatabaseException or CustomerAlreadyHasReservationException
                // both are checked exceptions
                //However, in lambda expression, checked exceptions cannot be thrown directly. that is why
                //I wrap the checked exception in an unchecked exception, like RuntimeException
                throw new RuntimeException(e);
            }
        });

        //saving new reservation data to database
        reservationRepository.save(modelMapper.map(reservationDTOToBeUpdated, Reservation.class));

        return reservationDTOToBeUpdated;
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
