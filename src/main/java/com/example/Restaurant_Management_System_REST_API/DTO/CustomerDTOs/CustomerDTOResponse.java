package com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CustomerDTOResponse {


    private Long id;
    private LocalDateTime creationTime;
    private Reservation reservation;
    private ContactDetails contactDetails;

    //Below are fields for security issues
    private String password;
    private Boolean accountNonExpired;//to avoid hardcoding I established these fields
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;
    private String emailAddress;
    private Set<Authority> authorities;
}
