package com.example.demo.controller;

import com.example.demo.base.BaseResponse;
import com.example.demo.dto.authaccountDTO.response.LoginAccountResponse;

import com.example.demo.service.oauth2.AuthFacebookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Facebook Authentication")
@RequiredArgsConstructor
public class AuthFacebookController {

    private final AuthFacebookService authFacebookService;

    @GetMapping("/authorize/facebook")
    public void redirectToFacebook(HttpServletResponse response) throws IOException {
        authFacebookService.facebookLoginUrl(response);
    }

    @GetMapping("/callback/facebook")
    public BaseResponse<LoginAccountResponse> facebookLogin(
            @RequestParam("code") String code,
            HttpServletResponse response) {
        LoginAccountResponse data = authFacebookService.loginWithFacebook(code, response);
        return new BaseResponse<>(HttpStatus.OK.value(), "Facebook login success", data);
    }
}
