package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRecordRepository extends CrudRepository<MenuRecord, Long> {

    Optional<MenuRecord> findByName(String name);
    List<MenuRecord> findByIsAvailable(boolean isAvailable);
    List<MenuRecord> findByCategory(Category category);
}
