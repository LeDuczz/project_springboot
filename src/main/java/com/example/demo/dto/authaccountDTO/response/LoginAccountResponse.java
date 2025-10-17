package com.example.demo.dto.authaccountDTO.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginAccountResponse {
    @NotNull(message = "Token is null")
    private String token;
}
