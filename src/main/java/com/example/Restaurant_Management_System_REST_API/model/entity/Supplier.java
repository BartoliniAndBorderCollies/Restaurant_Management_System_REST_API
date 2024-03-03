package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference //Supplier and InventoryItem have bidirectional relationship and both have getters and setters.
    // They were calling each other when Jackson was used to serialize the object causing StackOverFlow.//
    //I use the @JsonManagedReference and @JsonBackReference annotations to tell Jackson how to handle the serialization.
    // In this case, @JsonManagedReference is the side that Jackson will serialize, and @JsonBackReference is the side
    // that Jackson will omit to avoid the infinite loop.
    private List<InventoryItem> inventoryItemList;
}
