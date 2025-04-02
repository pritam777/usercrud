package com.user.api.service;

import com.user.api.dto.*;
import com.user.api.entity.User;
import com.user.api.repository.UserRepository;
import com.user.api.security.JwtUtil;
import com.user.api.security.TokenBlacklistService;

import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

/**
 * Service for validating JWT tokens, checking blacklisting, 
 * extracting claims, and resolving roles and permissions.
 * <p>
 * Used by /validate-token API to decode and validate user access tokens.
 * </p>
 * 
 * Includes built-in role-to-permission mapping.
 * 
 * @author Pritam Singh
 */
@Service
public class TokenService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenBlacklistService blacklistService;
    
    /**
     * Static map defining role-to-permission mappings.
     */
    private static final Map<String, List<String>> ROLE_PERMISSION_MAP = Map.of(
        "USER", List.of("read:user"),
        "ADMIN", List.of("read:user", "create:user", "update:user", "delete:user")
    );

    /**
     * Constructs a new TokenService instance.
     *
     * @param jwtUtil utility for token parsing and validation
     * @param userRepository user repository for fetching users
     * @param blacklistService service for tracking blacklisted tokens
     */
    public TokenService(JwtUtil jwtUtil, UserRepository userRepository, TokenBlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.blacklistService = blacklistService;
    }
    
    /**
     * Validates a JWT token, returning token metadata and user info if valid.
     *
     * @param token the JWT token string
     * @return structured response with token validity and claims
     */
    public TokenValidationResponse validateToken(String token) {
    	
        if (blacklistService.isBlacklisted(token)) {
            return TokenValidationResponse.builder()
                .valid(false)
                .message("Token has been expired or invalidated")
                .build();
        }
    	
        try {
            Claims claims = jwtUtil.parseTokenClaims(token);
            String email = claims.getSubject();

            Date issuedAtDate = claims.getIssuedAt();
            Date expirationDate = claims.getExpiration();
            List<String> roles = claims.get("roles", List.class);

            if (roles == null || roles.isEmpty()) {
                roles = List.of("USER");
            }
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            List<RolePermission> rolePermissions = roles.stream()
                .map(role -> RolePermission.builder()
                    .name(role)
                    .permissions(ROLE_PERMISSION_MAP.getOrDefault(role, List.of()))
                    .build())
                .toList();

            long remaining = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

            return TokenValidationResponse.builder()
                .valid(true)
                .userUuid(user.getUuid())
                .email(email)
                .issuedAt(toLocalDateTime(issuedAtDate))
                .expiresAt(toLocalDateTime(expirationDate))
                .remainingTimeInSeconds(remaining)
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

    /**
     * Converts a {@link Date} to {@link LocalDateTime} using system default zone.
     *
     * @param date date to convert
     * @return converted LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
