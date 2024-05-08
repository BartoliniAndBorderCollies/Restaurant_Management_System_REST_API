package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrderMenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.RestaurantOrderMenuRecordRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantOrderMenuRecordService {

    private final RestaurantOrderMenuRecordRepository restaurantOrderMenuRecordRepository;

    //this method returns RestaurantOrderMenuRecords with the id of restaurantOrder which is going to be deleted. So this list
    //which will be returned are restaurantOrderMenuRecords which should be deleted.
    public List<RestaurantOrderMenuRecord> findRestaurantOrderMenuRecordsForDeletionByRestaurantOrderId(Long restaurantOrderId)  {
        Iterable<RestaurantOrderMenuRecord> restaurantOrderMenuRecordIterable = restaurantOrderMenuRecordRepository.findAll();

        List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordsToBeDeleted = new ArrayList<>();
        for (RestaurantOrderMenuRecord eachRestaurantOrderMenuRecord: restaurantOrderMenuRecordIterable) {
            if(eachRestaurantOrderMenuRecord.getRestaurantOrder().getId().equals(restaurantOrderId)) {
                restaurantOrderMenuRecordsToBeDeleted.add(eachRestaurantOrderMenuRecord);
            }
        }
        return restaurantOrderMenuRecordsToBeDeleted;
    }

    public void deleteGivenRestaurantOrderMenuRecords(List<RestaurantOrderMenuRecord> restaurantOrderMenuRecordsToBeDeleted) {
        for (RestaurantOrderMenuRecord eachRecord: restaurantOrderMenuRecordsToBeDeleted) {
            restaurantOrderMenuRecordRepository.delete(eachRecord);
        }
    }
}
