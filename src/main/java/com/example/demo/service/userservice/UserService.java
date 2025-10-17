package com.example.demo.service.userservice;

import com.example.demo.dto.UserProfileDTO;

import java.util.List;

public interface UserService {
    UserProfileDTO getCurrentUserProfile();
    List<UserProfileDTO> getAllUsers();

}
