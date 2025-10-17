package com.example.demo.service.jwtservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${secret_key}")
    private String secretKey ;

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getTypeFromToken(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getClaims(token));
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String extractedUsername = getUsernameFromToken(token);
        return extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String generateAccessToken(String username,
                                      Collection<String> roles,
                                      Collection<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("permission", permissions);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

    }

    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

    }

    public List<String> getRolesFromToken(String token) {
        Object r = getClaims(token).get("roles");
        if (r instanceof Map<?,?> map) {
            Object roleObj = map.get("role");
            if (roleObj instanceof Collection<?> collection) {
                return collection.stream()
                        .map(Object::toString)
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    public List<String> getPermissionsFromToken(String token) {
        Object r = getClaims(token).get("roles");
        if (r instanceof Map<?,?> map) {
            Object permissionObj = map.get("permission");
            if (permissionObj instanceof Collection<?> collection) {
                return collection.stream()
                        .map(Object::toString)
                        .toList();
            }
        }
        return Collections.emptyList();
    }

}
