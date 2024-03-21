package com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs;

import com.example.Restaurant_Management_System_REST_API.DTO.ContactDetailsDTO.ContactDetailsDTO;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CustomerReservationDTO {


    private Long id;
    private ContactDetailsDTO contactDetails;
    private String emailAddress;
}
