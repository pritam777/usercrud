package com.user.api.dto;

import lombok.*;

/**
 * Request DTO used for validating a JWT token.
 * Contains the token string to be verified.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationRequest {
    private String token;
}
