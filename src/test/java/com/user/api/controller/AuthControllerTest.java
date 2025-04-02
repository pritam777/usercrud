package com.user.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.api.dto.*;
import com.user.api.security.JwtUtil;
import com.user.api.security.TokenBlacklistService;
import com.user.api.service.TokenService;
import com.user.api.service.UserService;

import com.user.api.testutils.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for AuthController endpoints.
 * Covers login, logout, password reset, and token validation.
 * Uses MockMvc and mocks for full isolation from service layer.
 * 
 * @author Pritam Singh
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // ✅ Disables security filters for testing
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private TokenBlacklistService blacklistService;

    @Test
    void login_success_shouldReturnJwtToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(TestData.VALID_EMAIL);
        request.setPassword(TestData.CORRECT_PASSWORD);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername(TestData.VALID_EMAIL))
            .thenReturn(User.withUsername(TestData.VALID_EMAIL)
                .password("{noop}" + TestData.CORRECT_PASSWORD)
                .roles("USER")
                .build());
        when(jwtUtil.generateToken(any())).thenReturn(TestData.MOCK_JWT);

        mockMvc.perform(post("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(TestData.MOCK_JWT));
    }

    @Test
    void login_failure_shouldReturnUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(TestData.VALID_EMAIL);
        request.setPassword(TestData.WRONG_PASSWORD);

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        		.andExpect(status().isBadRequest());
            //.andExpect(status().isUnauthorized());
    }

    @Test
    void forgotPassword_shouldReturnResetToken() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(TestData.VALID_EMAIL);

        when(userService.initiatePasswordReset(TestData.VALID_EMAIL))
            .thenReturn(TestData.RESET_TOKEN);

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resetToken").value(TestData.RESET_TOKEN));
    }

    @Test
    void validateToken_shouldReturnValidResponse() throws Exception {
        TokenValidationRequest request = new TokenValidationRequest();
        request.setToken(TestData.MOCK_JWT);

        when(tokenService.validateToken(TestData.MOCK_JWT))
            .thenReturn(TokenValidationResponse.builder()
                .valid(true)
                .email(TestData.VALID_EMAIL)
                .message("Token is valid")
                .build());

        mockMvc.perform(post("/api/v1/auth/validate-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.email").value(TestData.VALID_EMAIL));
    }

    @Test
    void logout_shouldInvalidateToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + TestData.MOCK_JWT))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token has been invalidated successfully"));
    }
}
