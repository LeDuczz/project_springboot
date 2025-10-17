package com.example.demo.dto.authaccountDTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoginRequest {
    @NotBlank(message = "Phone number can't be blank")
    @Length(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Password can't be blank")
    @Length(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
