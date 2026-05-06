package com.group3.backend.controller;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.UpdateUserProfileRequest;
import com.group3.backend.entity.User;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.service.SupabaseTokenService;

/**
 * REST controller for managing user profile and account endpoints.
 * Supports retrieving a user profile, updating editable profile fields,
 * and deleting a user account. Paths are exposed under both {@code /users}
 * and {@code /api/users} for compatibility with the mobile client.
 */

@RestController
@RequestMapping({ "/users", "/api/users" })
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;
    private final SupabaseTokenService tokenService;

    public UserController(UserRepository userRepository, SupabaseTokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<User> getUserProfile(@PathVariable UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateUserProfile(@PathVariable UUID id,
            @RequestBody UpdateUserProfileRequest request) {
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = existingUser.get();
        user.setFullName(request.getFullName().trim());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}/account")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable UUID id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    public ResponseEntity<User> getMyProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<UUID> userId = resolveUserIdFromAuth(authHeader);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return userRepository.findById(userId.get())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me/profile")
    public ResponseEntity<User> updateMyProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateUserProfileRequest request) {
        Optional<UUID> userId = resolveUserIdFromAuth(authHeader);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return updateUserProfile(userId.get(), request);
    }

    @DeleteMapping("/me/account")
    public ResponseEntity<Void> deleteMyAccount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<UUID> userId = resolveUserIdFromAuth(authHeader);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return deleteUserAccount(userId.get());
    }

    private Optional<UUID> resolveUserIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring("Bearer ".length());
        Map<String, Object> claims = tokenService.validateToken(token);
        if (claims == null) {
            return Optional.empty();
        }
        Object sub = claims.get("sub");
        if (sub == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(sub.toString()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
