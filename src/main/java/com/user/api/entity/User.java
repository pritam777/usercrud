package com.user.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 * This class is mapped to the 'users' table in the database.
 * 
 * Contains personal and authentication-related fields such as
 * name, email, password, UUID, and token metadata.
 * 
 * Used across authentication, registration, and role-assignment flows.
 * 
 * @author Pritam Singh
 */

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /**
     * Auto-generated primary key (not exposed in API responses).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public-facing UUID for the user, used in API responses.
     */
    @Column(name = "uuid", nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;
    
    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Unique email address of the user (used as username).
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Hashed password for authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Reset token used for password recovery.
     */
    @Column(name = "reset_token")
    private String resetToken;

    /**
     * Timestamp of when the user was created.
     */
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
