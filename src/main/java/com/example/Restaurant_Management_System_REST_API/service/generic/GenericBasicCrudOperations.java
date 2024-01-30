package com.example.Restaurant_Management_System_REST_API.service.generic;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GenericBasicCrudOperations<T, K, ID> {

    public T create(K object);
    public T findById(ID id) throws NotFoundInDatabaseException;
    public List<T> findAll();
    public T update(ID id, K object) throws NotFoundInDatabaseException;
    public ResponseEntity<?> delete(ID id) throws NotFoundInDatabaseException;
}
