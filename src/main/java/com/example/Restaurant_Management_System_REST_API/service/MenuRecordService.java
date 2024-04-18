package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs.MenuRecordDTOResponse;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.model.entity.MenuRecord;
import com.example.Restaurant_Management_System_REST_API.repository.MenuRecordRepository;
import com.example.Restaurant_Management_System_REST_API.service.generic.GenericBasicCrudOperations;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuRecordService implements GenericBasicCrudOperations<MenuRecordDTOResponse, MenuRecordDTORequest, Long> {

    private final MenuRecordRepository menuRecordRepository;
    private final ModelMapper modelMapper;

    @Override
    public MenuRecordDTOResponse create(MenuRecordDTORequest menuRecordDTORequest) {
        MenuRecord menuRecord = modelMapper.map(menuRecordDTORequest, MenuRecord.class);
        menuRecordRepository.save(menuRecord);

        return modelMapper.map(menuRecord, MenuRecordDTOResponse.class);
    }

    @Override
    public MenuRecordDTOResponse findById(Long menuRecordId) throws NotFoundInDatabaseException {
        MenuRecord menuRecord = menuRecordRepository.findById(menuRecordId).orElseThrow(() ->
                new NotFoundInDatabaseException(MenuRecord.class));

        return modelMapper.map(menuRecord, MenuRecordDTOResponse.class);
    }

    @Override
    public List<MenuRecordDTOResponse> findAll() {
        List<MenuRecordDTOResponse> list = new ArrayList<>();
        menuRecordRepository.findAll().forEach(menuRecord ->
                list.add(modelMapper.map(menuRecord, MenuRecordDTOResponse.class))
        );
        return list;
    }

    @Override
    public MenuRecordDTOResponse update(Long id, MenuRecordDTORequest updatedMenuRecordDTO) throws NotFoundInDatabaseException {
        MenuRecordDTOResponse menuRecordDTOResponse = findById(id);

        Optional.ofNullable(updatedMenuRecordDTO.getName()).ifPresent(menuRecordDTOResponse::setName);
        Optional.ofNullable(updatedMenuRecordDTO.getDescription()).ifPresent(menuRecordDTOResponse::setDescription);
        Optional.ofNullable(updatedMenuRecordDTO.getPrice()).ifPresent(menuRecordDTOResponse::setPrice);
        Optional.ofNullable(updatedMenuRecordDTO.getIngredients()).ifPresent(menuRecordDTOResponse::setIngredients);
        Optional.ofNullable(updatedMenuRecordDTO.getCategory()).ifPresent(menuRecordDTOResponse::setCategory);
        Optional.ofNullable(updatedMenuRecordDTO.getIsAvailable()).ifPresent(menuRecordDTOResponse::setIsAvailable);

        menuRecordRepository.save(modelMapper.map(menuRecordDTOResponse, MenuRecord.class));

        return menuRecordDTOResponse;
    }

    @Override
    public ResponseEntity<?> delete(Long id) throws NotFoundInDatabaseException {
        MenuRecord menuRecord = menuRecordRepository.findById(id).orElseThrow(() ->
                new NotFoundInDatabaseException(MenuRecord.class));
        menuRecordRepository.delete(menuRecord);

        return new ResponseEntity<>("Menu record has been deleted!", HttpStatus.OK);
    }

    MenuRecord findByName(String name) throws NotFoundInDatabaseException {
        return menuRecordRepository.findByName(name).orElseThrow(()-> new NotFoundInDatabaseException(MenuRecord.class));
    }
}
