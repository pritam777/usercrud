package com.user.api.dto;

import lombok.*;

/**
 * Request DTO for initiating password reset by email.
 * Contains the user's email address.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    private String email;
}
