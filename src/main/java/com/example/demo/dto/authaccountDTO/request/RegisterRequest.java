package com.example.demo.dto.authaccountDTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {
    @NotNull(message = "Full name can't be null")
    private String fullName;

    @NotBlank(message = "Password can't be blank")
    @Length(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Gender can't be null")
    private String gender;

    @NotBlank(message = "Phone number can't be blank")
    @Length(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;
}
