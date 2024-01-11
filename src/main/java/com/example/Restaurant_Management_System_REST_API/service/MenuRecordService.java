package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuRecordService implements GenericBasicCrudOperations<MenuRecord, MenuRecord, Long>{
    @Override
    public MenuRecord create(MenuRecord record) {
        return null;
    }

    @Override
    public MenuRecord read(Long id) {
        return null;
    }

    @Override
    public List<?> readAll() {
        return null;
    }

    @Override
    public MenuRecord update(MenuRecord record) {
        return null;
    }

    @Override
    public void delete(MenuRecord record) {

    }
}
