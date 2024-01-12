package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Supplier extends ContactDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "supplier")
    private ArrayList<InventoryItem> inventoryItemList;
}
