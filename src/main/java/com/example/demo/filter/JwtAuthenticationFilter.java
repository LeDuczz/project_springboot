package com.example.demo.filter;

import com.example.demo.service.jwtservice.CustomUserDetailsService;
import com.example.demo.service.jwtservice.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    private static final String EXCEPTION_ATTRIBUTE = "exception";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String path = request.getServletPath();
        if (path.startsWith("/api/auth") || authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);

        final String username;

        try {
            username = jwtService.getUsernameFromToken(token);
        } catch (Exception e){
            request.setAttribute(EXCEPTION_ATTRIBUTE, new BadCredentialsException("Invalid JWT token format"));
            throw new BadCredentialsException("Invalid JWT token format");
        }

        if(username == null){
            request.setAttribute(EXCEPTION_ATTRIBUTE, new BadCredentialsException("Invalid JWT token"));
            throw new BadCredentialsException("Invalid JWT token");
        }

        if(SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if(!jwtService.isTokenValid(token, userDetails)){
                request.setAttribute(EXCEPTION_ATTRIBUTE, new BadCredentialsException("JWT token is expired or invalid"));
                throw new BadCredentialsException("JWT token is expired or invalid");
            }

            List<String> roles = jwtService.getRolesFromToken(token);
            List<String> permissions = jwtService.getPermissionsFromToken(token);

            Set<GrantedAuthority> authorities = new HashSet<>();
            roles.forEach(role -> authorities.add(() -> role));
            permissions.forEach(permission -> authorities.add(() -> permission));


            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
