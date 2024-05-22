package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrderMenuRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantOrderMenuRecordRepository extends CrudRepository<RestaurantOrderMenuRecord, Long> {
    List<RestaurantOrderMenuRecord> findRestaurantOrderMenuRecordByRestaurantOrderId(Long restaurantOrderId);

}