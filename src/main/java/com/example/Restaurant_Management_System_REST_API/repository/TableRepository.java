package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends CrudRepository<Table, Long> {

    Optional<Table> findById(Long id);

    List<Table> findByIsAvailable(boolean isAvailable);
}
