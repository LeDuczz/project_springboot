package com.example.demo.seed;

import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Gender;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<String> defaultRoles = List.of("ROLE_USER", "ROLE_ADMIN");
        defaultRoles.forEach(role -> {
            if (roleRepository.findByRoleName(role).isEmpty()) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setRoleName(role);
                roleRepository.save(roleEntity);
            }
        });

        String adminPhone = "0123456789";
        if (!userRepository.existsByPhoneNumber(adminPhone)) {
            RoleEntity adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            UserEntity adminUser = new UserEntity();
            adminUser.setPhoneNumber(adminPhone);
            adminUser.setFullName("Admin User");
            adminUser.setGender(Gender.OTHER);
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setRole(Collections.singleton(adminRole));
            adminUser.setAuthProvider(com.example.demo.enums.AuthProvider.LOCAL);
            userRepository.save(adminUser);
        }
    }
}
