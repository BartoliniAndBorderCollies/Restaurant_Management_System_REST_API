package com.example.Restaurant_Management_System_REST_API.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderMenuRecordRepository extends JpaRepository<RestaurantOrderMenuRecord, Long> {
}