package com.webhook.userservice.service;

import com.webhook.userservice.exception.UserAlreadyExistsException;
import com.webhook.userservice.model.UserEntity;
import com.webhook.userservice.payload.UserRequest;
import com.webhook.userservice.payload.UserResponse;
import com.webhook.userservice.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Called on first login via Google OAuth2.
     * Auto-provisions user from Google JWT claims if not present.
     */
    public UserResponse registerOrFetchUser(Jwt jwt) {
        String email = jwt.getClaim("email");
        Optional<UserEntity> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return toResponse(existing.get());
        }
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setName(jwt.getClaim("name"));
        user.setGivenName(jwt.getClaim("given_name"));
        user.setFamilyName(jwt.getClaim("family_name"));
        user.setIsEmailVerified(Boolean.TRUE.equals(jwt.getClaim("email_verified")));
        user.setPreferredUsername(email);
        UserEntity saved = userRepository.save(user);
        return toResponse(saved);
    }

    /**
     * Exposed as an internal REST endpoint so webhook-service can verify user existence by email.
     */
    public UserResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toResponse(user);
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getIsEmailVerified());
    }
}
