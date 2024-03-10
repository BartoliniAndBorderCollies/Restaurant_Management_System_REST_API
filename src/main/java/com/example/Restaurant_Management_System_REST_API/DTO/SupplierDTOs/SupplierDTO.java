package com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupplierDTO {

    private Long id;
    private ContactDetails contactDetails;
    private List<InventoryItem> inventoryItemList;
}
