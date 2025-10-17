package com.example.demo.controller;

import com.example.demo.base.BaseResponse;
import com.example.demo.dto.authaccountDTO.request.LoginRequest;
import com.example.demo.dto.authaccountDTO.request.RegisterRequest;
import com.example.demo.dto.authaccountDTO.response.LoginAccountResponse;
import com.example.demo.dto.authaccountDTO.response.RegisterAccountResponse;
import com.example.demo.service.authaccountservice.AuthAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthAccountController {

    private final AuthAccountService authAccountService;

    @PostMapping("/register")
    public BaseResponse<RegisterAccountResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterAccountResponse data = authAccountService.register(registerRequest);
        return new BaseResponse<>(HttpStatus.OK.value(), "Registration Successful", data);
    }

    @PostMapping("/login")
    public BaseResponse<LoginAccountResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginAccountResponse data = authAccountService.login(loginRequest, response);
        return new BaseResponse<>(HttpStatus.OK.value(), "Login success", data);
    }

    @PostMapping("/logout")
    public BaseResponse<Object> logout(HttpServletResponse response) {
        authAccountService.logout(response);
        return new BaseResponse<>(HttpStatus.OK.value(), "Logout successful", null);
    }

    @PostMapping("/refresh-token")
    public BaseResponse<LoginAccountResponse> refreshToken(HttpServletRequest request) {
        LoginAccountResponse data = authAccountService.refreshToken(request);
        return new BaseResponse<>(HttpStatus.OK.value(), "Refresh token success", data);
    }

}
