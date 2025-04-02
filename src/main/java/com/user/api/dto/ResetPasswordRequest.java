package com.user.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Request DTO for resetting user password using a reset token.
 * Includes email, token, and the new password.
 * Uses validation annotations to enforce constraints.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Reset token must be provided")
    private String token;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
