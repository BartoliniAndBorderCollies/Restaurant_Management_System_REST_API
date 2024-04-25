package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import com.example.Restaurant_Management_System_REST_API.model.entity.Ingredient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MenuRecordForOrderDTO {

    private Long id;
    private String name;
    private Double portionsAmount;
    private List<Ingredient> ingredients;

}
