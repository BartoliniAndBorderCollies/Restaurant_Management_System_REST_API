package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MenuRecordDTORequest extends CatalogItem {

    public MenuRecordDTORequest(String name, String description, Double price, Long id, Set<String> ingredients,
                                Category category, Boolean isAvailable) {
        super(name, description, price);
        this.id = id;
        this.ingredients = ingredients;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    private Long id;
    private Set<String> ingredients;
    private Category category;
    private Boolean isAvailable;
}
