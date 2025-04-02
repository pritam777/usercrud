package com.user.api.controller;

import com.user.api.dto.*;
import com.user.api.security.JwtUtil;
import com.user.api.security.TokenBlacklistService;
import com.user.api.service.TokenService;
import com.user.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller handling authentication-related operations like login, logout,
 * password reset, and token validation.
 *
 * @author Pritam Singh
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserService userService,
                          TokenService tokenService,
                          TokenBlacklistService blacklistService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenService = tokenService;
        this.blacklistService = blacklistService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login request payload containing email and password
     * @return a JWT token in a map response
     */
    @Operation(summary = "Authenticate user and issue JWT")
    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request for email: {}", request.getEmail());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        logger.info("Token issued for: {}", request.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Initiates the password reset flow by generating a reset token.
     *
     * @param request the payload containing the user's email
     * @return a reset token in a map response
     */
    @Operation(summary = "Initiate password reset and return reset token")
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok(Map.of("resetToken", token));
    }

    /**
     * Resets the user's password using a token and a new password.
     *
     * @param request payload containing email, token, and new password
     * @return a success message
     */
    @Operation(summary = "Reset password using a valid token")
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Password has been successfully reset"
        ));
    }

    /**
     * Validates a JWT token and returns token meta and roles.
     *
     * @param request the token validation request
     * @return token validation response
     */
    @Operation(summary = "Validate JWT token and return role & scope")
    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody TokenValidationRequest request) {
        return ResponseEntity.ok(tokenService.validateToken(request.getToken()));
    }

    /**
     * Logs out a user by blacklisting the JWT token.
     *
     * @param request the HTTP request with Authorization header
     * @return a success or error message
     */
    @Operation(summary = "Logout user by blacklisting the JWT token")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.blacklist(token);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Token has been invalidated successfully"
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "message", "No token found in Authorization header"
        ));
    }
}
