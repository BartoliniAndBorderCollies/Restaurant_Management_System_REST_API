package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends CrudRepository<Supplier, Long> {

    Optional<Supplier> findByContactDetails_NameAndContactDetails_Street(String name, String street);
    List<Supplier> findByContactDetails_Name(String name);
    List<Supplier> findByContactDetails_City(String cityName);
}
