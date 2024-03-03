package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Supplier {

    public Supplier(Long id, ContactDetails contactDetails, List<InventoryItem> inventoryItemList) {
        this.id=id;
        this.contactDetails=contactDetails;
        this.inventoryItemList=inventoryItemList;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Embedded
    @Valid
    private ContactDetails contactDetails;

    @OneToMany(mappedBy = "supplier")
    private List<InventoryItem> inventoryItemList;
}
