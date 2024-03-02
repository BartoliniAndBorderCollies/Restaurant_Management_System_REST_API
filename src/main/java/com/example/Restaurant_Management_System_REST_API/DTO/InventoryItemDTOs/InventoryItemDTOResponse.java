package com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemDTOResponse extends CatalogItem {

    public InventoryItemDTOResponse(Long id, LocalDateTime deliveryDate, int stockAmount, Supplier supplier,
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
    private Supplier supplier; //TODO: change to DTO

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItemDTOResponse that = (InventoryItemDTOResponse) o;
        return stockAmount == that.stockAmount && Objects.equals(id, that.id) && Objects.equals(deliveryDate, that.deliveryDate) && Objects.equals(supplier, that.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deliveryDate, stockAmount, supplier);
    }
}
