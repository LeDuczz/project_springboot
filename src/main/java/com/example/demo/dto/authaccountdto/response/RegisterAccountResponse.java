package com.example.demo.dto.authaccountdto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterAccountResponse {
    @NotNull(message = "Message is null")
    private String message;
}
