package com.user.api.dto;

import lombok.*;

/**
 * Request DTO for user login.
 * Contains email and password credentials.
 * 
 * @author Pritam Singh
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
