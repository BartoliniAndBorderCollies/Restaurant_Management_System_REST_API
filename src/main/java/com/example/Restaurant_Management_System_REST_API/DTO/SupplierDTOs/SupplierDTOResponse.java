package com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTOResponse {

    private Long id;
    private ContactDetails contactDetails;
    private List<InventoryItem> inventoryItemList;
}
