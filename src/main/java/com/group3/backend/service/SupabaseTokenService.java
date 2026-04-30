package com.group3.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseTokenService {
    private static final Logger logger = LoggerFactory.getLogger(SupabaseTokenService.class);

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    public Map<String, Object> validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                logger.error("Token is null or empty");
                return null;
            }

            DecodedJWT decodedToken = JWT.decode(token);

            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", decodedToken.getSubject());
            claims.put("email", decodedToken.getClaim("email").asString());
            claims.put("user_id", decodedToken.getSubject());

            logger.info("Token validated successfully for user: {}", decodedToken.getSubject());
            return claims;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return null;
        }
    }
}
