package com.example.demo.service.oauth2;

import com.example.demo.dto.authaccountdto.response.LoginAccountResponse;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.AuthProvider;
import com.example.demo.enums.Gender;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.jwtservice.JwtService;
import com.example.demo.utils.AuthUtils;
import com.example.demo.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthGoogleService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.user-info.uri}")
    private String userInfoUri;

    @Value("${google.token.uri}")
    private String tokenUri;

    public LoginAccountResponse loginWithGoogle(String code, HttpServletResponse response) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        Map<String, Object> googleTokenResponse = restTemplate.postForObject(
                tokenUri,
                request,
                Map.class
        );

        if (googleTokenResponse == null || !googleTokenResponse.containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token from Google");
        }

        String accessToken = (String) googleTokenResponse.get("access_token");

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        Map<String, Object> userInfo = restTemplate.exchange(
                userInfoUri,
                HttpMethod.GET,
                userInfoRequest,
                Map.class
        ).getBody();

        if (userInfo == null || !userInfo.containsKey("email")) {
            throw new RuntimeException("Cannot get user info from Google");
        }

        String email = (String) userInfo.get("email");
        String fullName = (String) userInfo.get("name");
        String gender = (String) userInfo.get("gender");
        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setFullName(fullName);
            Gender userGender = null;
            if (gender != null) {
                if (gender.equalsIgnoreCase("male")) {
                    userGender = Gender.MALE;
                } else if (gender.equalsIgnoreCase("female")) {
                    userGender = Gender.FEMALE;
                }
            } else {
                userGender = Gender.OTHER;
            }
            newUser.setGender(userGender);
            newUser.setAuthProvider(AuthProvider.GOOGLE);
            newUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));

            RoleEntity userRole = roleRepository.findByRoleName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            newUser.setRole(Collections.singleton(userRole));

            return userRepository.save(newUser);
        });

        String token = jwtService.generateAccessToken(
                user.getUserId().toString(),
                AuthUtils.getCurrentUserRoles(user),
                AuthUtils.getCurrentUserPermissions(user)
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getUserId().toString()
        );

        CookieUtils.addRefreshTokenCookie(response, refreshToken);

        return new LoginAccountResponse(token);
    }

}
