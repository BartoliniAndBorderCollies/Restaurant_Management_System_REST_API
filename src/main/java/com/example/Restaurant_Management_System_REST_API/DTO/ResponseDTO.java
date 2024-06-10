package com.example.Restaurant_Management_System_REST_API.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@Data
public class ResponseDTO {

    private String message;
    private HttpStatus status;
}
