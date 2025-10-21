package com.example.demo.controller;

import com.example.demo.base.BaseResponse;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.service.userservice.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public BaseResponse<UserProfileDTO> profile() {
        UserProfileDTO currentUser = userService.getCurrentUserProfile();
        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get current user successful",
                currentUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public BaseResponse<List<UserProfileDTO>> getAllUsers() {
        List<UserProfileDTO> currentUser = userService.getAllUsers();
        return new BaseResponse<>(HttpStatus.OK.value(),
                "Get current admin user successful",
                currentUser);
    }

}
