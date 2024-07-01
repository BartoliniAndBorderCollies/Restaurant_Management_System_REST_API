package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(id, supplier.id) && Objects.equals(contactDetails, supplier.contactDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contactDetails);
    }
}
