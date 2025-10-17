package com.example.demo.utils;

import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.UnauthorizedException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@UtilityClass
public class AuthUtils {
    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (UserDetails) authentication.getPrincipal();
        }
        throw new UnauthorizedException("User is not authenticated");
    }

    public Collection<String> getCurrentUserRoles(UserEntity user) {
        return user.getRole().stream().map(RoleEntity::getRoleName).toList();
    }

    public Collection<String> getCurrentUserPermissions(UserEntity user) {
         return user.getRole().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(PermissionEntity::getPermissionName)
                .toList();
    }


}
