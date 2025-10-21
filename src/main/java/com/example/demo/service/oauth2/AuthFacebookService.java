package com.example.demo.service.oauth2;

import com.example.demo.dto.authaccountdto.response.FacebookTokenResponse;
import com.example.demo.dto.authaccountdto.response.FacebookUserResponse;
import com.example.demo.dto.authaccountdto.response.LoginAccountResponse;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.AuthProvider;
import com.example.demo.enums.Gender;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.jwtservice.JwtService;
import com.example.demo.utils.AuthUtils;
import com.example.demo.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthFacebookService {

    @Value("${facebook.client.id}")
    private String clientId;

    @Value("${facebook.client.secret}")
    private String clientSecret;

    @Value("${facebook.redirect.uri}")
    private String redirectUri;

    @Value("${facebook.token.uri}")
    private String tokenUri;

    @Value("${facebook.user-info.uri}")
    private String userInfoUri;

    @Value("${facebook.auth.uri}")
    private String authUri;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public LoginAccountResponse loginWithFacebook(String code, HttpServletResponse response) {
        FacebookUserResponse fbUser = getUserInfo(code);

        String facebookId = fbUser.getId();
        String name = fbUser.getName();
        String email = fbUser.getEmail();
        String gender = fbUser.getGender();
        UserEntity user = userRepository.findByPhoneNumber(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email != null ? email : facebookId);
                    newUser.setFullName(name);
                    newUser.setAuthProvider(AuthProvider.FACEBOOK);
                    newUser.setPasswordHash("FACEBOOK_USER");
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
                    RoleEntity role = roleRepository.findByRoleName("ROLE_USER")
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
                    newUser.setRole(Collections.singleton(role));
                    return userRepository.save(newUser);
                });

        String accessToken = jwtService.generateAccessToken(
                user.getUserId().toString(),
                AuthUtils.getCurrentUserRoles(user),
                AuthUtils.getCurrentUserPermissions(user)
        );
        String refreshToken = jwtService.generateRefreshToken(
                user.getUserId().toString()
        );

        CookieUtils.addRefreshTokenCookie(response, refreshToken);

        return new LoginAccountResponse(accessToken);
    }

    public void facebookLoginUrl(HttpServletResponse response) throws IOException {
        String facebookLoginUrl = authUri
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + URLEncoder.encode("randomStringOrJson", StandardCharsets.UTF_8)
                + "&scope=email,public_profile";

        response.sendRedirect(facebookLoginUrl);
    }

    public FacebookUserResponse getUserInfo(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String tokenRequestUrl = tokenUri
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&client_secret=" + clientSecret
                + "&code=" + code;

        FacebookTokenResponse tokenResponse = restTemplate.getForObject(tokenRequestUrl, FacebookTokenResponse.class);
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new RuntimeException("Failed to get Facebook access token");
        }

        String accessToken = tokenResponse.getAccessToken();

        String userInfoRequestUrl = userInfoUri + "&access_token=" + accessToken;
        FacebookUserResponse userInfo = restTemplate.getForObject(userInfoRequestUrl, FacebookUserResponse.class);

        if (userInfo == null || userInfo.getId() == null) {
            throw new RuntimeException("Failed to get Facebook user info");
        }

        return userInfo;
    }
}
