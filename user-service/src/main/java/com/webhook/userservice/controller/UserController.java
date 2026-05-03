package com.webhook.userservice.controller;

import com.webhook.userservice.payload.UserResponse;
import com.webhook.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /users
     * Called after Google OAuth2 login to auto-register user from JWT claims.
     * No request body needed — all data comes from the Google JWT.
     */
    @PostMapping
    public ResponseEntity<UserResponse> registerUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        UserResponse response = userService.registerOrFetchUser(jwt);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /users/me
     * Returns the currently logged-in user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/internal/{email}
     * Internal endpoint called by webhook-service via OpenFeign to verify user existence.
     */
    @GetMapping("/internal/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }
}
