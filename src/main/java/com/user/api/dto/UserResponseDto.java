package com.user.api.dto;

import lombok.*;

/**
 * Response DTO returned after user registration or fetch.
 * Contains UUID, name, email, and a message.
 * This is the safe, exposed representation of the user entity.
 * 
 * Used across multiple APIs to return user details.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
	private String uuid;
    private String name;
    private String email;
    private String message;
}
