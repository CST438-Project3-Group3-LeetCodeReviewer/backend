package com.group3.backend.dto;

import java.util.UUID;

public class SyncOAuthUserRequest {
    private UUID id;
    private String email;
    private String fullName;
    private String oauthProvider;

    public SyncOAuthUserRequest() {
    }

    public SyncOAuthUserRequest(UUID id, String email, String fullName, String oauthProvider) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.oauthProvider = oauthProvider;
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
}
