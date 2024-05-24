package com.example.Restaurant_Management_System_REST_API.repository;

import com.example.Restaurant_Management_System_REST_API.model.OrderStatus;
import com.example.Restaurant_Management_System_REST_API.model.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RestaurantOrderRepository extends CrudRepository<RestaurantOrder, Long> {

    @Query("SELECT ro FROM RestaurantOrder ro WHERE ro.orderTime >= :startDateTime AND ro.orderTime < :endDateTime")
    List<RestaurantOrder> findByOrderTimeRange(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    List<RestaurantOrder> findByOrderStatus(OrderStatus orderStatus);

    List<RestaurantOrder> findByTableId(Long id);


}
