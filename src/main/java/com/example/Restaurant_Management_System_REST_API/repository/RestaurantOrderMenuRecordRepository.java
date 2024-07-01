package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrderMenuRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RestaurantOrderMenuRecordRepository extends CrudRepository<RestaurantOrderMenuRecord, Long> {
    List<RestaurantOrderMenuRecord> findRestaurantOrderMenuRecordByRestaurantOrderId(Long restaurantOrderId);

    @Query("SELECT romr FROM RestaurantOrderMenuRecord romr WHERE romr.restaurantOrder.orderTime >= :time_from AND romr.restaurantOrder.orderTime < :time_to")
    List<RestaurantOrderMenuRecord> findRestaurantOrderMenuRecordByTimePeriod(@Param("time_from") LocalDateTime timeFrom, @Param("time_to") LocalDateTime timeTo);
}