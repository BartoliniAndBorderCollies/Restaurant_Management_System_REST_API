package com.example.Restaurant_Management_System_REST_API.DTO.CustomerDTOs;

import com.example.Restaurant_Management_System_REST_API.model.ContactDetails;
import com.example.Restaurant_Management_System_REST_API.model.entity.Authority;
import com.example.Restaurant_Management_System_REST_API.model.entity.Reservation;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTORequest {

    private Long id;
    private LocalDateTime creationTime;
    private Reservation reservation;
    private ContactDetails contactDetails;

    //Below are fields for security issues
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,}$", message =
            "Password must have at least 5 characters, one digit, one lowercase letter, one uppercase letter, and " +
                    "a special character")
    private String password;
    //            (?=.*[0-9]) ensures there is at least one digit.
//            (?=.*[a-z]) ensures there is at least one lowercase letter.
//            (?=.*[A-Z]) ensures there is at least one uppercase letter.
//            (?=.*[@#$%^&+=]) ensures there is at least one special character.
//            (?=\\S+$) ensures there are no spaces.
//.           {5,} ensures there are at least 5 characters.
    private String emailAddress;
    private Set<Authority> authorities;

    //to avoid hardcoding I established these below fields
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Boolean enabled;

}
