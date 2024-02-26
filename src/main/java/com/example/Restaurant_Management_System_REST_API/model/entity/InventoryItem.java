package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InventoryItem extends CatalogItem {

    public InventoryItem(Long id, LocalDateTime deliveryDate, int stockAmount, Supplier supplier, String name,
                         String description, Double price ) {
        super(name, description, price);
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.stockAmount = stockAmount;
        this.supplier = supplier;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime deliveryDate;
    @Positive(message = "Stock amount must be above zero!")
    private int stockAmount;
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
