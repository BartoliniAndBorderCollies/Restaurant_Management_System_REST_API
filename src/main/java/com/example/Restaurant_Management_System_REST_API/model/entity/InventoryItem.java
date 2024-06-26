package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class InventoryItem extends CatalogItem {

    public InventoryItem(Long id, int amount, Supplier supplier, String name,
                         String description, Double price ) {
        super(name, description, price);
        this.id = id;
        this.amount = amount;
        this.supplier = supplier;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @PositiveOrZero(message = "Amount must be above zero!")
    private double amount;
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @JsonBackReference //Supplier and InventoryItem have bidirectional relationship and both have getters and setters.
    // They were calling each other when Jackson was used to serialize the object causing StackOverFlow.//
    //I use the @JsonManagedReference and @JsonBackReference annotations to tell Jackson how to handle the serialization.
    // In this case, @JsonManagedReference is the side that Jackson will serialize, and @JsonBackReference is the side
    // that Jackson will omit to avoid the infinite loop.
    @NotNull(message = "Supplier must be provided!")
    private Supplier supplier;
}
