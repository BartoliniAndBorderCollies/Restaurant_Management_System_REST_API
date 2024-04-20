package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Ingredient;
import com.example.Restaurant_Management_System_REST_API.model.entity.InventoryItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuRecordDTOResponse extends CatalogItem {

    private Long id;
    private String name;
    private List<Ingredient> ingredients;
    private Category category;
    private Boolean isAvailable;
    private List<InventoryItem> inventoryItems;
}
