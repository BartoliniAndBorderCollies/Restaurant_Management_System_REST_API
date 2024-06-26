package com.example.Restaurant_Management_System_REST_API.service.generic;

import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.exception.CustomerAlreadyHasReservationException;
import com.example.Restaurant_Management_System_REST_API.exception.NotEnoughIngredientsException;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;

import java.util.List;

public interface GenericBasicCrudOperations<T, K, ID> {

    T create(K object) throws NotFoundInDatabaseException, CustomerAlreadyHasReservationException, ObjectAlreadyExistException,
            NotEnoughIngredientsException;
    T findById(ID id) throws NotFoundInDatabaseException;
    List<T> findAll();
    T update(ID id, K object) throws NotFoundInDatabaseException;
    ResponseDTO delete(ID id) throws NotFoundInDatabaseException;
}
