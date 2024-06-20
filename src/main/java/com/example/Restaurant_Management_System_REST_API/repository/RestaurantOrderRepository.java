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

    @Query("SELECT ro FROM RestaurantOrder ro WHERE ro.table.id = :id AND ro.orderTime >= :startDateTime AND ro.orderTime < :endDateTime")
    List<RestaurantOrder> findByTableIdAndOrderTimeRange(@Param("id") Long id, @Param("startDateTime") LocalDateTime startDateTime,
                                                         @Param("endDateTime") LocalDateTime endDateTime);
    @Query("SELECT ro FROM RestaurantOrder ro WHERE ro.totalAmountToPay >= :amountFrom AND ro.totalAmountToPay < :amountTo")
    List<RestaurantOrder> findByTotalAmountToPayRange(@Param("amountFrom") double amountFrom, @Param("amountTo")double amountTo);

    @Query("SELECT ro FROM RestaurantOrder ro WHERE ro.orderTime >= :timeFrom AND ro.orderTime < :timeTo")
    List<RestaurantOrder> findByTimeRange(@Param("timeFrom")LocalDateTime timeFrom, @Param("timeTo") LocalDateTime timeTo);
}
