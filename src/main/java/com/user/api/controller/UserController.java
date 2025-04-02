package com.user.api.controller;

import com.user.api.entity.User;
import com.user.api.repository.UserRepository;
import com.user.api.service.UserService;
import com.user.api.dto.UpdateNameRequest;
import com.user.api.dto.UserRequestDto;
import com.user.api.dto.UserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for user operations like registration, retrieval, and lookup.
 * Handles interactions with user repository and user service.
 * 
 * @author Pritam Singh
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all registered users in the system.
     *
     * @return list of user response DTOs
     */
    @Operation(summary = "Get all registered users")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponseDto> response = users.stream()
            .map(user -> UserResponseDto.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .email(user.getEmail())
                .message("User data loaded")
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches a user by their email address.
     *
     * @param email the email to search for
     * @return user response DTO if found
     */
    @Operation(summary = "Find a user by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> testFindByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserResponseDto response = UserResponseDto.builder()
            .uuid(user.getUuid())
            .name(user.getName())
            .email(user.getEmail())
            .message("User fetched successfully")
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new user using the provided details.
     *
     * @param userRequestDto user details payload
     * @return the registered user response DTO
     */
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.registerUser(userRequestDto));
    }
    
    /**
     * Updates the name of an existing user using their email address.
     *
     * @param email the email of the user whose name is to be updated
     * @param request the request body containing the new name
     * @return a UserResponseDto with updated user info
     */	
    @PutMapping("/update-name/{email}")
    public ResponseEntity<UserResponseDto> updateUserName(
            @PathVariable String email,
            @Valid @RequestBody UpdateNameRequest request) {

        UserResponseDto response = userService.updateUserName(email, request.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user by their email address.
     *
     * @param email the email of the user to delete
     * @return success or error message
     */
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Map<String, Object>> deleteUserByEmail(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
