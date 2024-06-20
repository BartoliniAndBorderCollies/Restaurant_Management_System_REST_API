package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {

    List<Reservation> findByName(String name);
    List<Reservation> findByPeopleAmountGreaterThan(int peopleAmount);
    List<Reservation> findByPeopleAmountLessThan(int peopleAmount);
    @Query("SELECT r FROM Reservation r WHERE r.start >= :dateTime")
    List<Reservation> findByStartAndAfter(@Param("dateTime") LocalDateTime dateTime);
    List<Reservation> findByCustomer_ContactDetails_Name(String name);
    List<Reservation> findByTables_Id(Long id);
    @Query("SELECT r FROM Reservation r WHERE r.start >= :dateTime AND r.customer.contactDetails.name = :name")
    List<Reservation> findByCustomer_ContactDetails_NameAndByStart(@Param("name") String name, @Param("dateTime") LocalDateTime dateTimeFrom);
}
