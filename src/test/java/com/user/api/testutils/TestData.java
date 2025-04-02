package com.user.api.testutils;

/**
 * Centralized configuration class for test data used across the test suite.
 * Helps maintain consistency and avoids duplication of hardcoded strings.
 * 
 * Can be extended to include UUIDs, roles, permissions, etc.
 * 
 * @author Pritam Singh
 */
public class TestData {

    public static final String VALID_EMAIL = "celvin.kelin@example.co.in";
    public static final String CORRECT_PASSWORD = "hashed_password_6";
    public static final String WRONG_PASSWORD = "invalid_passward";
    public static final String MOCK_JWT = "mock-jwt-token";
    public static final String RESET_TOKEN = "reset-token-123";

    private TestData() {
        // Utility class; prevent instantiation
    }
}
