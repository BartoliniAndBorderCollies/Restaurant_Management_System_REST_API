package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrderMenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderMenuRecordRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantOrderMenuRecordService {

    private final RestaurantOrderMenuRecordRepository restaurantOrderMenuRecordRepository;

    public void deleteRestaurantOrderMenuRecordsByRestaurantOrderId(Long restaurantOrderId) {
        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordByRestaurantOrderId =
                restaurantOrderMenuRecordRepository.findRestaurantOrderMenuRecordByRestaurantOrderId(restaurantOrderId);
        for (RestaurantOrderMenuRecord eachRecord: restaurantOrderMenuRecordByRestaurantOrderId) {
            restaurantOrderMenuRecordRepository.delete(eachRecord);
        }
    }

    public RestaurantOrderMenuRecord save(RestaurantOrderMenuRecord record) {
        return restaurantOrderMenuRecordRepository.save(record);
    }
}
