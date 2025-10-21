package com.example.demo.controller;

import com.example.demo.base.BaseResponse;
import com.example.demo.dto.authaccountdto.response.LoginAccountResponse;
import com.example.demo.service.oauth2.AuthGoogleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth/google")
@Tag(name = "Google Authentication")
@RequiredArgsConstructor
public class AuthGoogleController {

    private final AuthGoogleService authGoogleService;

    @GetMapping("/callback")
    public BaseResponse<LoginAccountResponse> googleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        LoginAccountResponse loginResponse = authGoogleService.loginWithGoogle(code, response);
        return new BaseResponse<>(HttpStatus.OK.value(), "Login with Google successful", loginResponse);
    }
}
