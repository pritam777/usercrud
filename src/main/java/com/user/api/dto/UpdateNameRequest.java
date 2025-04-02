package com.user.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for updating a user's name.
 * Used in PUT /users/update-name/{email} endpoint.
 * 
 * Author: Pritam Singh
 */
public class UpdateNameRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    public UpdateNameRequest() {
    }

    public UpdateNameRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
