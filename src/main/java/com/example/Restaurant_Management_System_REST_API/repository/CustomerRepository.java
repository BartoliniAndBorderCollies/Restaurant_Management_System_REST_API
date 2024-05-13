package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Optional<Customer> findByEmailAddress(String email);
    @Query("SELECT c FROM Customer c JOIN c.authorities a WHERE a.name = :name")
    List<Customer> findByAuthorities_Authority_name(String name);
}
