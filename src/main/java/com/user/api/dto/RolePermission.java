package com.user.api.dto;

import lombok.*;

import java.util.List;

/**
 * DTO representing a role with its associated permissions.
 * Used in token validation responses to describe user's role access.
 * 
 * @author Pritam Singh
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {
    private String name;
    private List<String> permissions;
}
