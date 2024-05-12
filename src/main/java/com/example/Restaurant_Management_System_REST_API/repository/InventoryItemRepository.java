package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByName(String name);
    List<InventoryItem> findByAmountGreaterThan(double amount);
    List<InventoryItem> findByAmountLessThan(double amount);

}
