package com.example.Restaurant_Management_System_REST_API.service;

import com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs.InventoryItemDTORequest;
import com.example.Restaurant_Management_System_REST_API.DTO.ResponseDTO;
import com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs.SupplierDTO;
import com.example.Restaurant_Management_System_REST_API.exception.NotFoundInDatabaseException;
import com.example.Restaurant_Management_System_REST_API.exception.ObjectAlreadyExistException;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import com.example.Restaurant_Management_System_REST_API.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    public SupplierDTO add(SupplierDTO supplierDTO) throws ObjectAlreadyExistException {
        Supplier supplier = modelMapper.map(supplierDTO, Supplier.class);
        checkIfSupplierExist(supplier);
        supplierRepository.save(supplier);

        return modelMapper.map(supplier, SupplierDTO.class);
    }

    Supplier checkIfSupplierExist(InventoryItemDTORequest inventoryItemDTORequest) throws NotFoundInDatabaseException {
        Supplier supplier = null;
        if(inventoryItemDTORequest.getSupplier() != null) {
            supplier = supplierRepository.findByContactDetails_NameAndContactDetails_Street(inventoryItemDTORequest.getSupplier().getContactDetails().getName(),
                    inventoryItemDTORequest.getSupplier().getContactDetails().getStreet()).orElseThrow(() ->
                    new NotFoundInDatabaseException(Supplier.class));
        }
        return supplier;
    }

    public void checkIfSupplierExist(Supplier supplier) throws ObjectAlreadyExistException {
        if(supplierRepository.findByContactDetails_NameAndContactDetails_Street(supplier.getContactDetails().getName(),
                supplier.getContactDetails().getStreet()).isPresent()) {
            throw new ObjectAlreadyExistException(Supplier.class);
        }
    }

    public List<SupplierDTO> findAll () {
        List<SupplierDTO> DTOlist = new ArrayList<>();

        supplierRepository.findAll().forEach(supplier ->
            DTOlist.add(modelMapper.map(supplier, SupplierDTO.class)));

        return DTOlist;
    }

    public ResponseDTO deleteById(Long id) throws NotFoundInDatabaseException {
        Supplier supplierToDelete = supplierRepository.findById(id).orElseThrow(()->
                new NotFoundInDatabaseException(Supplier.class));

        supplierRepository.delete(supplierToDelete);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Supplier: " + supplierToDelete.getId() + " has been deleted!");
        responseDTO.setStatus(HttpStatus.OK);

        return responseDTO;
    }

}
