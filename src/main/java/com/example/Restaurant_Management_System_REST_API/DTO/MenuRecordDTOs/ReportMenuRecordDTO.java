package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import com.example.Restaurant_Management_System_REST_API.model.Category;
import com.example.Restaurant_Management_System_REST_API.model.entity.Ingredient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportMenuRecordDTO {

    private Long id;
    private String name;
    private List<Ingredient> ingredients;
    private Category category;
    private Boolean isAvailable;
}
