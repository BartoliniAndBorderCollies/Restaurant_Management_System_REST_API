package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import com.example.Restaurant_Management_System_REST_API.model.CatalogItem;
import com.example.Restaurant_Management_System_REST_API.model.Category;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuRecordDTOResponse extends CatalogItem {


    //TODO: add String name field in next branch
    private Long id;
    private Set<String> ingredients;
    private Category category;
    private Boolean isAvailable;
}
