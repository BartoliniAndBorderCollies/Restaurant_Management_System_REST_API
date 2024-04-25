package com.example.Restaurant_Management_System_REST_API.service.generic;

import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GenericBasicCrudOperations<T, K, ID> {

    T create(K object) throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException, ObjectAlreadyExistException;
    T findById(ID id) throws NotFoundInDatabaseException;
    List<T> findAll();
    T update(ID id, K object) throws NotFoundInDatabaseException;
    ResponseEntity<?> delete(ID id) throws NotFoundInDatabaseException;
}
