package com.example.Restaurant_Management_System_REST_API.DTO.InventoryItemDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.entity.Supplier;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Data
public class InventoryItemDTOResponse extends CatalogItem {

    public InventoryItemDTOResponse(Long id, int amount, Supplier supplier,
                                    String name, String description, Double price) {
        super(name, description, price);
        this.id = id;
        this.amount = amount;
        this.supplier = supplier;
    }

    private Long id;
    private int amount;
    private Supplier supplier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItemDTOResponse that = (InventoryItemDTOResponse) o;
        return amount == that.amount && Objects.equals(id, that.id) && Objects.equals(supplier, that.supplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, supplier);
    }
}
