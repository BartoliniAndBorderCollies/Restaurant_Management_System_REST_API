package com.example.Restaurant_Management_System_REST_API.DTO.SupplierDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.ContactDetailsDTO.ContactDetailsDTO;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class InventoryItemSupplierDTO {

    private Long id;
    private ContactDetailsDTO contactDetails;
}
