package com.group3.backend.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity representing an application user profile.
 * Stores account-related information used by the backend
 * for profile retrieval, updates, and account deletion.
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "oauth_provider", nullable = false)
    private String oauthProvider;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public User() {
    }

    public User(UUID id, String email, String fullName, String oauthProvider, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.oauthProvider = oauthProvider;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public void setOauthProvider(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}