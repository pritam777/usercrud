package com.user.api.service;

import com.user.api.entity.User;
import com.user.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.user.api.dto.UserRequestDto;
import com.user.api.dto.UserResponseDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public UserResponseDto registerUser(UserRequestDto dto) {
		logger.info("Registering new user with email: {}", dto.getEmail());
	    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
	    	logger.warn("Attempt to register user with existing email: {}", dto.getEmail());
	        throw new IllegalArgumentException("Email is already in use.");
	    }

	    User user = User.builder()
	    	.uuid(UUID.randomUUID().toString())
	        .name(dto.getName())
	        .email(dto.getEmail())
	        .password(passwordEncoder.encode(dto.getPassword()))
	        .createdAt(LocalDateTime.now())
	        .build();

	    User saved = userRepository.save(user);
	    logger.debug("User persisted with ID: {}", saved.getId());

	    return UserResponseDto.builder()
	        .uuid(saved.getUuid())
	        .name(saved.getName())
	        .email(saved.getEmail())
	        .message("User registered successfully")
	        .build();
	}

	
	public String initiatePasswordReset(String email) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    String token = UUID.randomUUID().toString();
	    user.setResetToken(token);
	    userRepository.save(user);

	    // Normally you'd send this token via email
	    return token;
	}

	public void resetPassword(String email, String token, String newPassword) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new RuntimeException("User not found"));

	    if (user.getResetToken() == null || !token.equals(user.getResetToken())) {
	        throw new IllegalArgumentException("Invalid or expired reset token");
	    }

	    user.setPassword(passwordEncoder.encode(newPassword));
	    user.setResetToken(null); // Clear token after use
	    userRepository.save(user);
	}

	
	public UserResponseDto updateUserName(String email, String newName) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

	    user.setName(newName);
	    userRepository.save(user);

	    return UserResponseDto.builder()
	            .uuid(user.getUuid())
	            .name(user.getName())
	            .email(user.getEmail())
	            .message("User name updated successfully")
	            .build();
	}
	
	public void deleteUserByEmail(String email) {
	    User user = userRepository.findByEmail(email)
	        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

	    userRepository.delete(user);
	}


}