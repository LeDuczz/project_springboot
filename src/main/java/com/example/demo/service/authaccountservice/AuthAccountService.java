package com.example.demo.service.authaccountservice;

import com.example.demo.dto.authaccountdto.request.LoginRequest;
import com.example.demo.dto.authaccountdto.request.RegisterRequest;
import com.example.demo.dto.authaccountdto.response.LoginAccountResponse;
import com.example.demo.dto.authaccountdto.response.RegisterAccountResponse;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.AuthProvider;
import com.example.demo.enums.Gender;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.jwtservice.CustomUserDetailsService;
import com.example.demo.service.jwtservice.JwtService;
import com.example.demo.utils.AuthUtils;
import com.example.demo.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthAccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customerUserDetailsService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;


    public RegisterAccountResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new BadRequestException("Phone number already in use");
        }

        UserEntity user = new UserEntity();
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setFullName(registerRequest.getFullName());
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setGender(Gender.valueOf(registerRequest.getGender().toUpperCase()));
        Optional<RoleEntity> role = roleRepository.findByRoleName("ROLE_USER");
        user.setRole(role.map(Collections::singleton).orElseThrow(()
                -> new ResourceNotFoundException("Default role not found")));
        userRepository.save(user);
        return new RegisterAccountResponse("Registration successful");
    }

    public LoginAccountResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        UserEntity user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new BadRequestException("Invalid phone number or password"));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getPhoneNumber(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid phone number or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getUserId().toString(),
                AuthUtils.getCurrentUserRoles(user),
                AuthUtils.getCurrentUserPermissions(user));

        String refreshToken = jwtService.generateRefreshToken(user.getUserId().toString());
        CookieUtils.addRefreshTokenCookie(response, refreshToken);

        return new LoginAccountResponse(accessToken);

    }

    public void logout(HttpServletResponse response) {
        CookieUtils.clearRefreshTokenCookie(response);
    }

    public LoginAccountResponse refreshToken(HttpServletRequest request) {

        String token = CookieUtils.readRefreshTokenFromCookie(request);

        if (token == null) {
            throw new UnauthorizedException("Token is missing");
        }

        try {
            String type = jwtService.getTypeFromToken(token);
            if (!"refresh".equals(type)) {
                throw new UnauthorizedException("Invalid token type");
            }

            String username = jwtService.getUsernameFromToken(token);
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                throw new UnauthorizedException("Token is invalid or expired");
            }

            UserEntity user = userRepository.findByUserId(UUID.fromString(username))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String newAccessToken = jwtService.generateAccessToken(userDetails.getUsername(),
                    AuthUtils.getCurrentUserRoles(user),
                    AuthUtils.getCurrentUserPermissions(user));

            return new LoginAccountResponse(newAccessToken);

        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token");
        }

    }



}
