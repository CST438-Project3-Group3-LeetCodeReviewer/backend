package com.group3.backend.controller;

import com.group3.backend.dto.SyncOAuthUserRequest;
import com.group3.backend.entity.User;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.service.SupabaseTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final SupabaseTokenService tokenService;
    private final UserRepository userRepository;

    public AuthController(SupabaseTokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring("Bearer ".length());
        Map<String, Object> claims = tokenService.validateToken(token);

        if (claims == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid token"));
        }

        return ResponseEntity.ok(claims);
    }

    @GetMapping("/test-token")
    public ResponseEntity<?> getTestToken() {
        return ResponseEntity.ok(Map.of("test_token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXItaWQiLCJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20ifQ.test"));
    }

    @PostMapping("/sync-user")
    public ResponseEntity<?> syncOAuthUser(@RequestBody SyncOAuthUserRequest request) {
        if (request.getId() == null || request.getEmail() == null || request.getOauthProvider() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        User user = userRepository.findById(request.getId())
                .orElse(new User());

        user.setId(request.getId());
        user.setEmail(request.getEmail());
        user.setOauthProvider(request.getOauthProvider());
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(java.time.LocalDateTime.now());
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
}
