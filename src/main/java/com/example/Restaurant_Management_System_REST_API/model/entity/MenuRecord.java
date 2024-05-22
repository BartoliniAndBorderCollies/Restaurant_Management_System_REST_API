package com.example.Restaurant_Management_System_REST_API.model.entity;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MenuRecord extends CatalogItem {

    public MenuRecord(List<Ingredient> ingredients, Category category, String name, String description, Double price, Boolean isAvailable) {
        super(name, description, price); // This calls the constructor in CatalogItem
        this.ingredients = ingredients;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ElementCollection // element of collections will be stored in separate table, and below I define this table
    @CollectionTable(name = "ingredients", joinColumns = @JoinColumn(name = "menu_record_id"))
    private List<Ingredient> ingredients;
    @NotNull(message = "Category is missing!")
    @Column(nullable = false)
    private Category category;
    @NotNull(message = "Availability is missing!")
    @Column(nullable = false)
    private Boolean isAvailable; //I use object to be able to hold null values (for updating process, I want to update
    //just fields which hold values, if some are skipped they should not be changed on db. if it was a primitive data type
    //of boolean then it would change to false as default
    @OneToMany(mappedBy = "menuRecord")
    private List<RestaurantOrderMenuRecord> menuRecords;
}
