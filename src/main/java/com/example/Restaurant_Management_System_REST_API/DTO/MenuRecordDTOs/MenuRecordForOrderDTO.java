package com.example.Restaurant_Management_System_REST_API.DTO.MenuRecordDTOs;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MenuRecordForOrderDTO {

    private Long id;
    private String name;
    private Double portionsAmount;
}
