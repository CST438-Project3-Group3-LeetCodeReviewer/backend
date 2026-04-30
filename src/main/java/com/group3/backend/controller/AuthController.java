package com.group3.backend.controller;

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

    public AuthController(SupabaseTokenService tokenService) {
        this.tokenService = tokenService;
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
}
