package com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemDTORequest extends CatalogItem {

    private Long id;
    private LocalDateTime deliveryDate;
    private int stockAmount;
    private Supplier supplier;
}
