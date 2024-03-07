package com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CustomerDTOReservationResponse {


    private Long id;
    private ContactDetails contactDetails;
    private String emailAddress;
}
