package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.ReservationDTOs.ReservationDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.TableNotAvailableException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import com.example.Restaurant_Management_System_REST_API.repository.ReservationRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class ReservationService implements GenericBasicCrudOperations<ReservationDTO, ReservationDTO, Long> {

    private final ReservationRepository reservationRepository;
    private final ModelMapper modelMapper;
    private final CustomerService customerService;
    private final TableService tableService;

    @Override
    @Transactional(rollbackFor = NotFoundInDatabaseException.class)
    public ReservationDTO create(ReservationDTO reservationDTO) throws NotFoundInDatabaseException,
            CustomerAlreadyHasReservationException {
        Reservation reservation = modelMapper.map(reservationDTO, Reservation.class);

        assignCustomerToReservationAndSave(reservation);
        if (reservation.getTables() != null)
            checkIfTablesAreAvailable(reservation);

        iterateAndSetTablesToReservation(reservation);

        return modelMapper.map(reservation, ReservationDTO.class);
    }

    private void checkIfTablesAreAvailable(Reservation reservation) throws NotFoundInDatabaseException {
        tableService.checkIfTablesAreAvailable(reservation);
    }

    private void iterateAndSetTablesToReservation(Reservation reservation) throws NotFoundInDatabaseException {
        if (reservation.getTables() != null && reservation.getTables().size() > 0) {
            tableService.iterateAndSetTablesToReservationAndSave(reservation);
        }
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
    @Transactional
    public ReservationDTO update(Long id, ReservationDTO reservationDTORequest) throws NotFoundInDatabaseException {
        ReservationDTO existingReservationDTO = findById(id);

        // Update fields if they are not null
        Optional.ofNullable(reservationDTORequest.getName()).ifPresent(existingReservationDTO::setName);
        Optional.ofNullable(reservationDTORequest.getDescription()).ifPresent(existingReservationDTO::setDescription);
        Optional.of(reservationDTORequest.getPeopleAmount()).ifPresent(existingReservationDTO::setPeopleAmount);
        Optional.ofNullable(reservationDTORequest.getStart()).ifPresent(existingReservationDTO::setStart);

        // If tables are not null, check their availability and update them
        Optional.ofNullable(reservationDTORequest.getTables()).ifPresent(tables -> {
            Reservation reservation = modelMapper.map(reservationDTORequest, Reservation.class);
            try {
                checkIfTablesAreAvailableAndUpdate(reservation);
            } catch (NotFoundInDatabaseException e) {
                throw new TableNotAvailableException();
            }
            existingReservationDTO.setTables(tables);
        });

        // Save the updated reservation
        reservationRepository.save(modelMapper.map(existingReservationDTO, Reservation.class));

        return existingReservationDTO;
    }

    private void checkIfTablesAreAvailableAndUpdate(Reservation reservation) throws NotFoundInDatabaseException {
            checkIfTablesAreAvailable(reservation);
            tableService.iterateAndSetTablesToReservationAndSave(reservation);
    }

    @Override
    public ResponseDTO delete(Long id) throws NotFoundInDatabaseException {
        Reservation reservationToDelete = reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(Reservation.class));

        if (reservationToDelete.getTables() != null)
            tableService.iterateAndSetReservationToNullInTablesAndSave(reservationToDelete);//because Table is owning side
        reservationRepository.delete(reservationToDelete);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Reservation: " + reservationToDelete.getName() + " has been successfully deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }
}
