package com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemDTORequest extends CatalogItem {

    public InventoryItemDTORequest (Long id, LocalDateTime deliveryDate, int stockAmount, Supplier supplier,
                                    String name, String description, Double price) {
        super(name, description, price);
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.stockAmount = stockAmount;
        this.supplier = supplier;
    }

    private Long id;
    private LocalDateTime deliveryDate;
    private int stockAmount;
    private Supplier supplier;//TODO: change to DTO
}
