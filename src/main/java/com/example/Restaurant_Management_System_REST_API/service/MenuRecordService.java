package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuRecordService implements GenericBasicCrudOperations<MenuRecord, MenuRecord, Long> {

    private MenuRecordRepository menuRecordRepository;

    @Override
    public MenuRecord create(MenuRecord menuRecord) {
        return menuRecordRepository.save(menuRecord);
    }

    @Override
    public MenuRecord findById(Long menuRecordId) throws NotFoundInDatabaseException {
        return menuRecordRepository.findById(menuRecordId).orElseThrow(() ->
                new NotFoundInDatabaseException(MenuRecord.class));
    }

    @Override
    public List<MenuRecord> findAll() {
        Iterable<MenuRecord> iterable = menuRecordRepository.findAll();
        List<MenuRecord> list = new ArrayList<>();

        for (MenuRecord menuRecord : iterable) {
            list.add(menuRecord);
        }
//        iterable.forEach(list::add); a shorter version
        return list;
    }

    @Override
    public MenuRecord update(Long id, MenuRecord updatedMenuRecord) throws NotFoundInDatabaseException {
        MenuRecord menuRecord = findById(id);

        Optional.ofNullable(updatedMenuRecord.getName()).ifPresent(menuRecord::setName);
        Optional.ofNullable(updatedMenuRecord.getDescription()).ifPresent(menuRecord::setDescription);
        Optional.ofNullable(updatedMenuRecord.getPrice()).ifPresent(menuRecord::setPrice);
        Optional.ofNullable(updatedMenuRecord.getIngredients()).ifPresent(menuRecord::setIngredients);
        Optional.ofNullable(updatedMenuRecord.getCategory()).ifPresent(menuRecord::setCategory);
        Optional.ofNullable(updatedMenuRecord.getIsAvailable()).ifPresent(menuRecord::setIsAvailable);

        return menuRecordRepository.save(menuRecord);
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        MenuRecord menuRecord = findById(id);
        menuRecordRepository.delete(menuRecord);

        return new ResponseEntity<>("Menu record has been deleted!", HttpStatus.OK);
    }
}
