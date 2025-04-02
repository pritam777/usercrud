package com.user.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.user.api.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.user.api.dto.RolePermission;
import com.user.api.dto.TokenValidationResponse;
import com.user.api.repository.UserRepository;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.Key;
import java.time.ZoneId;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "nztM7RLZCrAEMPl2oFdqz2RcylymYJ7cOAV74L8zQzE=";

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parseTokenClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    
    private static final Map<String, List<String>> ROLE_PERMISSION_MAP = Map.of(
    	    "USER", List.of("read:user"),
    	    "ADMIN", List.of("read:user", "create:user", "update:user", "delete:user")
    	);

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .claim("roles", List.of("USER"))
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    
    public TokenValidationResponse validateToken(String token, UserRepository userRepository) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // implement getSigningKey() to return your Key
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            Date issuedAtDate = claims.getIssuedAt();
            Date expirationDate = claims.getExpiration();

            List<String> roles = claims.get("roles", List.class); // assuming roles are stored in claims

            // Fetch UUID from DB
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Map roles to RolePermission DTOs
            List<RolePermission> rolePermissions = roles.stream()
                .map(role -> RolePermission.builder()
                    .name(role)
                    .permissions(ROLE_PERMISSION_MAP.getOrDefault(role, List.of()))
                    .build())
                .toList();

            long remainingTime = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

            return TokenValidationResponse.builder()
                .valid(true)
                .userUuid(user.getUuid()) // assuming it's a String
                .email(email)
                .issuedAt(issuedAtDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .expiresAt(expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .remainingTimeInSeconds(remainingTime)
                .roles(rolePermissions)
                .message("Token is valid")
                .build();

        } catch (Exception e) {
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Invalid or expired token")
                .build();
        }
    }

    
    
}
