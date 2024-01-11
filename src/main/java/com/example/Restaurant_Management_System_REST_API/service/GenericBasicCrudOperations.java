package com.example.Restaurant_Management_System_REST_API.service;

import java.util.List;

public interface GenericBasicCrudOperations<T, K, ID> {

    public T create(K object);
    public T read(ID id);
    public List<?> readAll();
    public T update(K object);
    public void delete(K object);
}
