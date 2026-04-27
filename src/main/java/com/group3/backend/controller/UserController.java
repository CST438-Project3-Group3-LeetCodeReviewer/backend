package com.group3.backend.controller;

import com.group3.backend.dto.UpdateUserProfileRequest;
import com.group3.backend.entity.User;
import com.group3.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing user profile and account endpoints.
 * Supports retrieving a user profile, updating editable profile fields,
 * and deleting a user account.
 */

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}