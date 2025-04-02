package com.user.api.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for token validation.
 * Contains token metadata, user info, roles, and validity message.
 * 
 * Used in the /validate-token API response.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {
	private boolean valid;
	private String userUuid; 
	private String email;
	private LocalDateTime issuedAt;
	private LocalDateTime expiresAt;
	private long remainingTimeInSeconds;
	private List<RolePermission> roles;
	private String message;
}
