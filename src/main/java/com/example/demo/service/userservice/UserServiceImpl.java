package com.example.demo.service.userservice;

import com.example.demo.dto.UserProfileDTO;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Primary
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getCurrentUserProfile() {
        UserDetails currentUser = AuthUtils.getCurrentUser();
        UUID userId;
        try {
            userId = UUID.fromString(currentUser.getUsername());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for user ID: " + currentUser.getUsername());
        }
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserId Not Found At getCurrentUserProfile()" + currentUser.getUsername()));

        return convertUserProfileDTO(userEntity);
    }

    private UserProfileDTO convertUserProfileDTO(UserEntity userEntity) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(userEntity.getUserId());
        userProfileDTO.setPhoneNumber(userEntity.getPhoneNumber());
        userProfileDTO.setFullName(userEntity.getFullName());
        userProfileDTO.setGender(userEntity.getGender().toString());
        userProfileDTO.setRole(userEntity.getRole().stream().map(RoleEntity::getRoleName).toList());
        return userProfileDTO;
    }

    @Override
    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertUserProfileDTO).toList();
    }

}
